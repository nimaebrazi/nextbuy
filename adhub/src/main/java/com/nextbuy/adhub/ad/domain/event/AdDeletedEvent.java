package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;

public record AdDeletedEvent(
        AdId adId,
        Instant occurredAt
) implements AdDomainEvent {
}
