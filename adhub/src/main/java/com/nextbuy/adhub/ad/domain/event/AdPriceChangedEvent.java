package com.nextbuy.adhub.ad.domain.event;

import java.time.Instant;

import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.shared.domain.Mony;

public record AdPriceChangedEvent(
        AdId adId,
        Mony price,
        Instant occurredAt
) implements AdDomainEvent {
}
