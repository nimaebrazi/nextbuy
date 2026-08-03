package com.nextbuy.adhub.ad.application.command.approve;

import java.time.Instant;
import java.util.UUID;

public record AdApproveResult(
        UUID adId,
        String status,
        Instant updatedAt
) {
}
