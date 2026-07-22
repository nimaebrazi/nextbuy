package com.nextbuy.adhub.ad.application.command.create;

import java.time.Instant;
import java.util.UUID;

public record AdCreatedResult(
        UUID id,
        String status,
        Instant createdAt
) {
}
