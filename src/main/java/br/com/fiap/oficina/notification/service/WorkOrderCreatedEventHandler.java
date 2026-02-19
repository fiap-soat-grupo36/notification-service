package br.com.fiap.oficina.notification.service;

import br.com.fiap.oficina.notification.event.WorkOrderCreatedEvent;

public interface WorkOrderCreatedEventHandler {

    void handle(WorkOrderCreatedEvent event);
}
