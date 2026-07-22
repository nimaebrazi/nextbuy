package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;

public record AdUpdatedEvent(
        AdId adId,
        String title,
        String description,
        Instant occurredAt
) implements AdDomainEvent {
}
