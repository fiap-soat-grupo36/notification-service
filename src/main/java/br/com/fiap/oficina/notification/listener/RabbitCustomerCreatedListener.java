package br.com.fiap.oficina.notification.listener;

import br.com.fiap.oficina.notification.event.CustomerCreatedEvent;
import br.com.fiap.oficina.notification.service.CustomerCreatedEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitCustomerCreatedListener {

    private final ObjectMapper objectMapper;
    private final CustomerCreatedEventHandler customerCreatedEventHandler;

    @RabbitListener(queues = "${app.messaging.customer-created.queue}")
    public void consumeCustomerCreated(Map<String, Object> payload) {
        CustomerCreatedEvent event = objectMapper.convertValue(payload, CustomerCreatedEvent.class);
        log.info("Evento customer.created recebido para customerId={}", event.customerId());
        customerCreatedEventHandler.handle(event);
    }
}
