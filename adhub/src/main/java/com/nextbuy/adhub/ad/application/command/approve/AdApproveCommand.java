package com.nextbuy.adhub.ad.application.command.approve;

import java.util.UUID;

public record AdApproveCommand(
        UUID adId
) {
}
