package br.com.fiap.oficina.notification.event;

public record CustomerCreatedEvent(
        Long customerId,
        String name,
        String email
) {
}
