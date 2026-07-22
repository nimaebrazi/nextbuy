package com.nextbuy.adhub.shared.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot<ID, E extends DomainEvent> extends BaseEntity<ID> {

    private final List<E> domainEvents = new ArrayList<>();

    protected void publishEvent(E event) {
        domainEvents.add(event);
    }

    public List<E> pullDomainEvents() {
        List<E> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }
}
