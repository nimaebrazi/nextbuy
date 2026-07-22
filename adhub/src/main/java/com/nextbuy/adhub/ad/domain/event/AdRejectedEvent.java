package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;

public record AdRejectedEvent(
        AdId adId,
        String reason,
        Instant occurredAt
) implements AdDomainEvent {
}
