package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;

public record AdTitleChangedEvent(
        AdId adId,
        String title,
        Instant occurredAt
) implements AdDomainEvent {
}
