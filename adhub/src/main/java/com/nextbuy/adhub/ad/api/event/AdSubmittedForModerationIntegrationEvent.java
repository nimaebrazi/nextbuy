package com.nextbuy.adhub.ad.api.event;

import java.time.Instant;
import java.util.UUID;

public record AdSubmittedForModerationIntegrationEvent(
        UUID adId,
        String previousStatus,
        Instant occurredAt
) implements AdIntegrationEvent {
}
