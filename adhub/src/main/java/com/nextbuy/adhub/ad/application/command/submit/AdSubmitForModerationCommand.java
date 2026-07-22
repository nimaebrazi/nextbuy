package com.nextbuy.adhub.ad.application.command.submit;

import java.util.UUID;

public record AdSubmitForModerationCommand(
        UUID adId,
        long ownerId
) {
}
