package br.com.fiap.oficina.notification.listener;

import br.com.fiap.oficina.notification.event.WorkOrderCreatedEvent;
import br.com.fiap.oficina.notification.service.WorkOrderCreatedEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitWorkOrderCreatedListener {

    private final ObjectMapper objectMapper;
    private final WorkOrderCreatedEventHandler workOrderCreatedEventHandler;

    @Value("${app.messaging.work-order-created.queue}")
    private String queueName;

    @RabbitListener(queues = "${app.messaging.work-order-created.queue}")
    public void consumeWorkOrderCreated(Map<String, Object> payload) {
        log.info("Mensagem recebida na fila {}. payloadKeys={}",
                queueName,
                payload == null ? "[]" : payload.keySet());

        try {
            WorkOrderCreatedEvent event = objectMapper.convertValue(payload, WorkOrderCreatedEvent.class);
            Long clientId = event.client() == null ? null : event.client().id();
            log.info("Evento work-order.created recebido para ordem={}, clienteId={}", event.workOrderId(), clientId);
            workOrderCreatedEventHandler.handle(event);
            log.info("Evento work-order.created processado com sucesso. ordem={}", event.workOrderId());
        } catch (Exception e) {
            log.error("Falha ao processar evento work-order.created na fila {}. payload={}", queueName, payload, e);
            throw e;
        }
    }
}
