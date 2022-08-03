package org.example.actorsapi.infrastructure.config;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.StringUtils;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.ReceiverOptions;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

import java.time.Duration;
import java.util.Optional;
import java.util.function.BiConsumer;

import static reactor.rabbitmq.BindingSpecification.binding;
import static reactor.rabbitmq.ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE;
import static reactor.rabbitmq.ExchangeSpecification.exchange;
import static reactor.rabbitmq.QueueSpecification.queue;

@Configuration
@Slf4j
public class RabbitConfiguration {

    @Bean
    public ConnectionFactory connectionFactory(RabbitProperties rabbitProperties) {
        final var connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setUsername(rabbitProperties.getUserName());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setAutomaticRecoveryEnabled(true);

        Optional
                .ofNullable(rabbitProperties.getPort())
                .ifPresent(connectionFactory::setPort);
        Optional
                .ofNullable(rabbitProperties.getVirtualHost())
                .filter(StringUtils::hasText)
                .ifPresent(connectionFactory::setVirtualHost);

        return connectionFactory;
    }

    @Bean
    public Sender sender(ConnectionFactory connectionFactory, MessagingProperties messagingProperties) {
        final var senderOptions = new SenderOptions().connectionFactory(connectionFactory);
        final var sender = RabbitFlux.createSender(senderOptions);

        sender
                .declare(
                        exchange(messagingProperties.getExchangeName())
                                .type(messagingProperties.getExchangeType())
                                .durable(true))
                .then(sender.declare(
                        queue(messagingProperties.getQueueName())
                                .durable(true)
                ))
                .then(sender.bind(
                        binding()
                                .exchange(messagingProperties.getExchangeName())
                                .queue(messagingProperties.getQueueName())
                                .routingKey(messagingProperties.getRoutingKey())
                ))
                .block(); //to force create the elements

        return sender;
    }

    @Bean
    @DependsOn("sender")
    public Receiver receiver(ConnectionFactory connectionFactory) {
        final var receivedOptions = new ReceiverOptions().connectionFactory(connectionFactory);
        return RabbitFlux.createReceiver(receivedOptions);
    }

    @Bean
    public BiConsumer<Receiver.AcknowledgmentContext, Exception> connectionRecovery() {
        return new ExceptionHandlers
                        .RetryAcknowledgmentExceptionHandler(
                            Duration.ofMinutes(3),
                            Duration.ofSeconds(5),
                            CONNECTION_RECOVERY_PREDICATE
                );
    }

}
