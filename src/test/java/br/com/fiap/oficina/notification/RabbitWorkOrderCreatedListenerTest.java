package br.com.fiap.oficina.notification;

import br.com.fiap.oficina.notification.event.WorkOrderCreatedEvent;
import br.com.fiap.oficina.notification.listener.RabbitWorkOrderCreatedListener;
import br.com.fiap.oficina.notification.service.WorkOrderCreatedEventHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitWorkOrderCreatedListenerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private WorkOrderCreatedEventHandler handler;

    @InjectMocks
    private RabbitWorkOrderCreatedListener listener;

    @Test
    void consumeWorkOrderCreated_deveDelegarParaHandler() {
        Map<String, Object> payload = Map.of("workOrderId", "os-123");
        WorkOrderCreatedEvent event = new WorkOrderCreatedEvent(
                "os-123",
                "RECEBIDA",
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(objectMapper.convertValue(payload, WorkOrderCreatedEvent.class)).thenReturn(event);

        listener.consumeWorkOrderCreated(payload);

        verify(handler).handle(event);
    }
}
