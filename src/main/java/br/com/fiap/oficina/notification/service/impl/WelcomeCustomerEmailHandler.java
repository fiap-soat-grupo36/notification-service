package br.com.fiap.oficina.notification.service.impl;

import br.com.fiap.oficina.notification.event.CustomerCreatedEvent;
import br.com.fiap.oficina.notification.service.CustomerCreatedEventHandler;
import br.com.fiap.oficina.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WelcomeCustomerEmailHandler implements CustomerCreatedEventHandler {

    private static final String EMAIL_SUBJECT = "Conta criada com sucesso";
    private static final String EMAIL_TEMPLATE = "cliente-cadastrado";

    private final EmailService emailService;

    @Override
    public void handle(CustomerCreatedEvent event) {
        String email = event.email();
        if (email == null || email.isBlank()) {
            log.warn("Evento customer.created ignorado por ausência de email. customerId={}", event.customerId());
            return;
        }

        String customerName = Objects.requireNonNullElse(event.name(), "cliente");
        Map<String, Object> variaveis = Map.of(
                "nomeCliente", customerName,
                "customerId", Objects.requireNonNullElse(event.customerId(), 0L)
        );

        log.info("Preparando email de boas-vindas. customerId={}, to={}, template={}",
                event.customerId(), email, EMAIL_TEMPLATE);

        emailService.enviarEmail(
                email,
                EMAIL_SUBJECT,
                EMAIL_TEMPLATE,
                variaveis
        );
    }
}
