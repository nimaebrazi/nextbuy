package com.nextbuy.adhub.ad.api.event;

import java.time.Instant;
import java.util.UUID;

public record AdActivatedIntegrationEvent(
        UUID adId,
        Instant expiresAt,
        Instant occurredAt
) implements AdIntegrationEvent {
}
