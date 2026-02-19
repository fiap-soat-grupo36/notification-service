package br.com.fiap.oficina.notification;

import br.com.fiap.oficina.notification.event.WorkOrderCreatedEvent;
import br.com.fiap.oficina.notification.service.EmailService;
import br.com.fiap.oficina.notification.service.impl.WorkOrderCreatedEmailHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class WorkOrderCreatedEmailHandlerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private WorkOrderCreatedEmailHandler handler;

    @Test
    void handle_deveEnviarEmailQuandoEventoPossuiEmail() {
        WorkOrderCreatedEvent event = new WorkOrderCreatedEvent(
                "os-123",
                "RECEBIDA",
                LocalDateTime.now(),
                "Cliente solicitou revisão completa",
                new WorkOrderCreatedEvent.ClientData(1L, "João Silva", "joao@example.com", "11999999999"),
                new WorkOrderCreatedEvent.VehicleData(10L, "ABC-1234", "Honda", "Civic", 2020),
                List.of(new WorkOrderCreatedEvent.ServiceData(100L, "Troca de óleo", "Serviço de manutenção", BigDecimal.valueOf(150))),
                List.of(new WorkOrderCreatedEvent.ProductData(200L, 2, BigDecimal.valueOf(35)))
        );

        handler.handle(event);

        verify(emailService).enviarEmail(
                eq("joao@example.com"),
                eq("Ordem de serviço criada com sucesso"),
                eq("ordem-servico-criada"),
                anyMap()
        );
    }

    @Test
    void handle_naoDeveEnviarEmailQuandoClienteNaoTemEmail() {
        WorkOrderCreatedEvent event = new WorkOrderCreatedEvent(
                "os-123",
                "RECEBIDA",
                LocalDateTime.now(),
                null,
                new WorkOrderCreatedEvent.ClientData(1L, "João Silva", "", "11999999999"),
                null,
                List.of(),
                List.of()
        );

        handler.handle(event);

        verifyNoInteractions(emailService);
    }
}
