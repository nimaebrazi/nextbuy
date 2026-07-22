package com.nextbuy.adhub.ad.domain.model;

import java.util.Set;

public enum AdStatus {
    DRAFT,
    PENDING_MODERATION,
    ACTIVE,
    REJECTED,
    SUSPENDED,
    EXPIRED,
    DELETED;

    private Set<AdStatus> allowedTargets;

    static {
        DRAFT.allowedTargets = Set.of(PENDING_MODERATION, DELETED);
        PENDING_MODERATION.allowedTargets = Set.of(ACTIVE, REJECTED, DELETED);
        REJECTED.allowedTargets = Set.of(PENDING_MODERATION, DELETED);
        ACTIVE.allowedTargets = Set.of(PENDING_MODERATION, SUSPENDED, DELETED, EXPIRED);
        EXPIRED.allowedTargets = Set.of(PENDING_MODERATION, DELETED);
        SUSPENDED.allowedTargets = Set.of(PENDING_MODERATION, DELETED);
        DELETED.allowedTargets = Set.of();
    }

    public boolean mayBecome(AdStatus target) {
        return this == target || allowedTargets.contains(target);
    }

    public boolean canSubmitForModeration() {
        return switch (this) {
            case DRAFT, REJECTED, EXPIRED, SUSPENDED, PENDING_MODERATION -> true;
            default -> false;
        };
    }

    public boolean canSubmitSensitiveEdit() {
        return this == ACTIVE || this == PENDING_MODERATION;
    }
}
