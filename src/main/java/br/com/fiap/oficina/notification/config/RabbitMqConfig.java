package br.com.fiap.oficina.notification.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue customerCreatedQueue(@Value("${app.messaging.customer-created.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public DirectExchange customerEventsExchange(@Value("${app.messaging.customer-created.exchange}") String exchangeName) {
        return ExchangeBuilder.directExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Binding customerCreatedBinding(Queue customerCreatedQueue,
                                          DirectExchange customerEventsExchange,
                                          @Value("${app.messaging.customer-created.routing-key}") String routingKey) {
        return BindingBuilder.bind(customerCreatedQueue).to(customerEventsExchange).with(routingKey);
    }

    @Bean
    public Queue workOrderCreatedQueue(@Value("${app.messaging.work-order-created.queue}") String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    @Bean
    public DirectExchange workOrderEventsExchange(@Value("${app.messaging.work-order-created.exchange}") String exchangeName) {
        return ExchangeBuilder.directExchange(exchangeName).durable(true).build();
    }

    @Bean
    public Binding workOrderCreatedBinding(Queue workOrderCreatedQueue,
                                           DirectExchange workOrderEventsExchange,
                                           @Value("${app.messaging.work-order-created.routing-key}") String routingKey) {
        return BindingBuilder.bind(workOrderCreatedQueue).to(workOrderEventsExchange).with(routingKey);
    }

    @Bean
    public MessageConverter rabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
