package com.nextbuy.adhub.ad.application.command.submit;

import java.time.Instant;
import java.util.UUID;

public record AdSubmitForModerationResult(
        UUID adId,
        String status,
        Instant submittedAt
) {
}
