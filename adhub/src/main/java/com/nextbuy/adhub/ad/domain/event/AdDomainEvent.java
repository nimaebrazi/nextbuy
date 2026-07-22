package com.nextbuy.adhub.ad.domain.event;

import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.shared.domain.DomainEvent;

/**
 * Events raised by the {@code Ad} aggregate. Implementations are records
 * carrying an immutable snapshot of the facts at the moment they occurred —
 * never a live reference to the aggregate.
 */
public sealed interface AdDomainEvent extends DomainEvent
        permits AdActivatedEvent,
        AdDeletedEvent,
        AdExpiredEvent,
        AdPriceChangedEvent,
        AdRejectedEvent,
        AdSubmittedForModerationEvent,
        AdSuspendedEvent,
        AdTitleChangedEvent,
        AdUpdatedEvent {

    AdId adId();
}
