package com.nextbuy.adhub.shared.domain;

import java.time.Instant;

/**
 * Marker for domain events: immutable facts, named in past tense.
 */
public interface DomainEvent {
    Instant occurredAt();
}
