package com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateAdResponse(
        UUID id,
        String status,
        Instant createdAt
) {
}
