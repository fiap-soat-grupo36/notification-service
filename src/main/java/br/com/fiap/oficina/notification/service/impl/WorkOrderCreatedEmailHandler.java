package br.com.fiap.oficina.notification.service.impl;

import br.com.fiap.oficina.notification.event.WorkOrderCreatedEvent;
import br.com.fiap.oficina.notification.service.EmailService;
import br.com.fiap.oficina.notification.service.WorkOrderCreatedEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkOrderCreatedEmailHandler implements WorkOrderCreatedEventHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String EMAIL_SUBJECT = "Ordem de serviço criada com sucesso";
    private static final String EMAIL_TEMPLATE = "ordem-servico-criada";

    private final EmailService emailService;

    @Override
    public void handle(WorkOrderCreatedEvent event) {
        String email = event.client() == null ? null : event.client().email();
        if (email == null || email.isBlank()) {
            log.warn("Evento work-order.created ignorado por ausência de email. ordem={}", event.workOrderId());
            return;
        }

        Long clientId = event.client() == null ? null : event.client().id();
        log.info("Preparando email de ordem de serviço. ordem={}, clienteId={}, to={}, status={}, template={}",
                event.workOrderId(), clientId, email, event.status(), EMAIL_TEMPLATE);

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("nomeCliente", event.client().name() == null ? "cliente" : event.client().name());
        variables.put("ordemServicoId", Objects.requireNonNullElse(event.workOrderId(), "-"));
        variables.put("statusOrdemServico", Objects.requireNonNullElse(event.status(), "RECEBIDA"));
        variables.put("dataCriacao", event.createdAt() == null ? "-" : DATE_TIME_FORMATTER.format(event.createdAt()));
        variables.put("observacoes", event.notes() == null || event.notes().isBlank() ? "Sem observações" : event.notes());
        variables.put("clienteTelefone", event.client().phone() == null ? "-" : event.client().phone());

        if (event.vehicle() != null) {
            variables.put("veiculoModelo", Objects.requireNonNullElse(event.vehicle().model(), "-"));
            variables.put("veiculoPlaca", Objects.requireNonNullElse(event.vehicle().plate(), "-"));
            variables.put("veiculoMarca", Objects.requireNonNullElse(event.vehicle().brand(), "-"));
            variables.put("veiculoAno", event.vehicle().year() == null ? "-" : event.vehicle().year().toString());
        } else {
            variables.put("veiculoModelo", "-");
            variables.put("veiculoPlaca", "-");
            variables.put("veiculoMarca", "-");
            variables.put("veiculoAno", "-");
        }

        variables.put("servicos", event.services() == null ? List.of() : event.services());
        variables.put("produtos", event.products() == null ? List.of() : event.products());

        emailService.enviarEmail(
                email,
                EMAIL_SUBJECT,
                EMAIL_TEMPLATE,
                variables
        );
    }
}
