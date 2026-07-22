package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;

public record AdActivatedEvent(
        AdId adId,
        Instant expiresAt,
        Instant occurredAt
) implements AdDomainEvent {
}
