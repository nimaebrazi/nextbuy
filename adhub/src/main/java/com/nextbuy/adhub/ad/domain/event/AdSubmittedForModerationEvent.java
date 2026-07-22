package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdStatus;

public record AdSubmittedForModerationEvent(
        AdId adId,
        AdStatus previousStatus,
        Instant occurredAt
) implements AdDomainEvent {
}
