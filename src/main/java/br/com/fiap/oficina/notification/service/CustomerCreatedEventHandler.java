package br.com.fiap.oficina.notification.service;

import br.com.fiap.oficina.notification.event.CustomerCreatedEvent;

public interface CustomerCreatedEventHandler {

    void handle(CustomerCreatedEvent event);
}
