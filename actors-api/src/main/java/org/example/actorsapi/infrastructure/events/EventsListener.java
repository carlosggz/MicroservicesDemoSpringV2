package org.example.actorsapi.infrastructure.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.actorsapi.application.LikeHandler;
import org.example.actorsapi.domain.events.LikeMovieEvent;
import org.example.actorsapi.infrastructure.config.MessagingProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.Receiver;

import java.util.function.BiConsumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventsListener {

    private final LikeHandler handler;
    private final ObjectMapper objectMapper;

    @Bean
    public DisposableBean likeListener(Receiver receiver,
                                     BiConsumer<Receiver.AcknowledgmentContext, Exception> connectionRecovery,
                                     MessagingProperties messagingProperties) {
        final var likeConsumer = receiver
                .consumeNoAck(messagingProperties.getQueueName(),
                        new ConsumeOptions().exceptionHandler(connectionRecovery))
                .subscribe(this::processEvent);

        return likeConsumer::dispose;
    }

    private void processEvent(Delivery delivery) {
        try {
            var event = objectMapper.readValue(delivery.getBody(), LikeMovieEvent.class);
            handler.handle(event);
        }
        catch (Exception ex) {
            log.error("Error processing event: {}", ex.getMessage()) ;
            throw new RuntimeException(ex);
        }
    }
}
