package org.example.actorsapi.utils;

import com.github.fridujo.rabbitmq.mock.MockChannel;
import com.github.fridujo.rabbitmq.mock.MockConnection;
import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import com.github.fridujo.rabbitmq.mock.MockNode;
import com.github.fridujo.rabbitmq.mock.metrics.MetricsCollectorWrapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Command;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Method;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.AMQCommand;
import lombok.SneakyThrows;
import org.example.actorsapi.infrastructure.crud.ActorsCrudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
@Import({BaseIntegrationTest.TestConfig.class})
public abstract class BaseIntegrationTest {

    @Autowired
    protected ActorsCrudRepository crudRepository;

    @BeforeEach
    void setup() {
        crudRepository.deleteAll().block();
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        public ConnectionFactory mockConnectionFactory() {
            return new IntegrationMockConnectionFactory();
        }
    }

    public static class IntegrationMockConnectionFactory extends MockConnectionFactory {

        @Override
        public MockConnection newConnection() {
            var metricsCollectorWrapper = MetricsCollectorWrapper.Builder.build(this);
            return new IntegrationMockConnection(mockNode, metricsCollectorWrapper);
        }
    }

    public static class IntegrationMockConnection extends MockConnection {
        private final MockNode mockNode;
        private final MetricsCollectorWrapper metricsCollectorWrapper;

        public IntegrationMockConnection(MockNode mockNode, MetricsCollectorWrapper metricsCollectorWrapper) {
            super(mockNode, metricsCollectorWrapper);
            this.mockNode = mockNode;
            this.metricsCollectorWrapper = metricsCollectorWrapper;
        }

        @Override
        public MockChannel createChannel(int channelNumber) throws AlreadyClosedException {
            if (!isOpen()) {
                throw new AlreadyClosedException(new ShutdownSignalException(false, true, null, this));
            }
            return new IntegrationMockChannel(channelNumber, mockNode, this, metricsCollectorWrapper);
        }
    }

    public static class IntegrationMockChannel extends MockChannel {

        public IntegrationMockChannel(int channelNumber, MockNode node, MockConnection mockConnection,
                                      MetricsCollectorWrapper metricsCollectorWrapper) {
            super(channelNumber, node, mockConnection, metricsCollectorWrapper);
        }

        @Override
        public CompletableFuture<Command> asyncCompletableRpc(Method method) {
            if (method instanceof AMQP.Exchange.Declare) {
                return exchangeDeclare((AMQP.Exchange.Declare) method);
            }
            if (method instanceof AMQP.Queue.Declare) {
                return queueDeclare((AMQP.Queue.Declare) method);
            }
            if (method instanceof AMQP.Queue.Bind) {
                return queueBind((AMQP.Queue.Bind) method);
            }
            if (method instanceof AMQP.Exchange.Bind) {
                return exchangeBind((AMQP.Exchange.Bind) method);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeShutdownListener(ShutdownListener listener) {
        }

        @SneakyThrows
        private CompletableFuture<Command> exchangeDeclare(AMQP.Exchange.Declare declare) {
                return CompletableFuture.completedFuture(new AMQCommand(exchangeDeclare(
                        declare.getExchange(),
                        declare.getType(),
                        declare.getDurable(),
                        declare.getAutoDelete(),
                        declare.getInternal(),
                        declare.getArguments())));
        }

        private CompletableFuture<Command> queueDeclare(AMQP.Queue.Declare declare) {
            return CompletableFuture.completedFuture(new AMQCommand(queueDeclare(
                    declare.getQueue(),
                    declare.getDurable(),
                    declare.getExclusive(),
                    declare.getAutoDelete(),
                    declare.getArguments())));
        }

        private CompletableFuture<Command> queueBind(AMQP.Queue.Bind bind) {
            return CompletableFuture.completedFuture(new AMQCommand(queueBind(
                    bind.getQueue(),
                    bind.getExchange(),
                    bind.getRoutingKey(),
                    bind.getArguments())));
        }

        private CompletableFuture<Command> exchangeBind(AMQP.Exchange.Bind bind) {
            return CompletableFuture.completedFuture(new AMQCommand(exchangeBind(
                    bind.getDestination(),
                    bind.getSource(),
                    bind.getRoutingKey(),
                    bind.getArguments())));
        }
    }
}
