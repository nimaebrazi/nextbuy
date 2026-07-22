package com.nextbuy.adhub.ad.domain.model;

import com.nextbuy.adhub.ad.domain.event.AdActivatedEvent;
import com.nextbuy.adhub.ad.domain.event.AdDeletedEvent;
import com.nextbuy.adhub.ad.domain.event.AdDomainEvent;
import com.nextbuy.adhub.ad.domain.event.AdExpiredEvent;
import com.nextbuy.adhub.ad.domain.event.AdPriceChangedEvent;
import com.nextbuy.adhub.ad.domain.event.AdRejectedEvent;
import com.nextbuy.adhub.ad.domain.event.AdSubmittedForModerationEvent;
import com.nextbuy.adhub.ad.domain.event.AdSuspendedEvent;
import com.nextbuy.adhub.ad.domain.event.AdTitleChangedEvent;
import com.nextbuy.adhub.ad.domain.event.AdUpdatedEvent;
import com.nextbuy.adhub.ad.domain.exception.AdDomainException;
import com.nextbuy.adhub.shared.domain.AggregateRoot;
import com.nextbuy.adhub.shared.domain.Mony;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;


public final class Ad extends AggregateRoot<AdId, AdDomainEvent> {
    private static final Duration LIFETIME = Duration.ofDays(30);

    private final OwnerId ownerId;
    private final CategoryId categoryId;
    private final AdLocation location;

    private Title title;
    private Description description;
    private Mony price;
    private String rejectionReason;
    private String suspensionReason;
    private AdStatus status;

    private Instant createdAt;
    private Instant expiresAt;
    private Instant updatedAt;
    private Instant rejectedAt;
    private Instant suspendedAt;
    private Instant deletedAt;

    private Ad(
            AdId adId,
            OwnerId ownerId,
            CategoryId categoryId,
            AdLocation location,
            Title title,
            Description description,
            Mony price,
            String rejectionReason,
            String suspensionReason,
            AdStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant updatedAt,
            Instant rejectedAt,
            Instant suspendedAt,
            Instant deletedAt
    ) {

        if (adId != null && adId.isAssigned()) {
            assignIdentity(adId);
        }

        this.ownerId = ownerId;
        this.categoryId = categoryId;
        this.location = location;
        this.title = title;
        this.description = description;
        this.price = price;
        this.rejectionReason = rejectionReason;
        this.suspensionReason = suspensionReason;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.updatedAt = updatedAt;
        this.rejectedAt = rejectedAt;
        this.suspendedAt = suspendedAt;
        this.deletedAt = deletedAt;
    }

    public static Ad createDraft(
            OwnerId ownerId,
            CategoryId categoryId,
            String title,
            String description,
            Mony price,
            AdLocation location,
            Instant now
    ) {
        Objects.requireNonNull(ownerId, "ownerId is required");
        Objects.requireNonNull(categoryId, "categoryId is required");
        Objects.requireNonNull(price, "price is required");
        Objects.requireNonNull(location, "location is required");
        Objects.requireNonNull(now, "now is required");

        Title titleVo = new Title(title == null ? null : title.trim());
        Description descriptionVo = description == null ? null : new Description(description);

        return new Ad(
                AdId.unassigned(),
                ownerId,
                categoryId,
                location,
                titleVo,
                descriptionVo,
                price,
                null,
                null,
                AdStatus.DRAFT,
                now,
                null,
                now,
                null,
                null,
                null
        );
    }

    public static Ad reconstruct(
            AdId id,
            OwnerId ownerId,
            CategoryId categoryId,
            AdLocation location,
            Title title,
            Description description,
            Mony price,
            String rejectionReason,
            String suspensionReason,
            AdStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant updatedAt,
            Instant rejectedAt,
            Instant suspendedAt,
            Instant deletedAt
    ) {
        return new Ad(
                id,
                ownerId,
                categoryId,
                location,
                title,
                description,
                price,
                rejectionReason,
                suspensionReason,
                status,
                createdAt,
                expiresAt,
                updatedAt,
                rejectedAt,
                suspendedAt,
                deletedAt
        );
    }

    public void assignId(AdId assignedId) {
        if (getId().isAssigned()) {
            throw new AdDomainException.IdIsAlreadyAssigned();
        }
        if (assignedId == null || !assignedId.isAssigned()) {
            throw new AdDomainException.AssignedIdMustBeAssigned();
        }
        assignIdentity(assignedId);
    }

    public void submitForModeration(Instant now) {
        assertNotDeleted();
        Objects.requireNonNull(now, "now is required");

        if (!status.canSubmitForModeration()) {
            throw new AdDomainException.InvalidStatus(status.name(), AdStatus.PENDING_MODERATION.name());
        }

        AdStatus previousStatus = status;
        if (transitionTo(AdStatus.PENDING_MODERATION, now)) {
            publishEvent(new AdSubmittedForModerationEvent(getId(), previousStatus, now));
        }
    }

    public void submitSensitiveEdit(String newTitle, String newDescription, Instant now) {
        assertNotDeleted();
        ensureAssignedId();
        Objects.requireNonNull(now, "now is required");

        if (!status.canSubmitSensitiveEdit()) {
            throw new AdDomainException.InvalidStatus(status.name(), AdStatus.PENDING_MODERATION.name());
        }

        Title newTitleVo = new Title(newTitle == null ? null : newTitle.trim());
        Description newDescVo = newDescription == null ? null : new Description(newDescription);

        boolean detailsChanged = !title.equals(newTitleVo) || !Objects.equals(description, newDescVo);
        if (detailsChanged) {
            title = newTitleVo;
            description = newDescVo;
            updatedAt = now;
            publishEvent(new AdUpdatedEvent(getId(), title.value(), description != null ? description.value() : null, now));
        }

        AdStatus previousStatus = status;
        if (transitionTo(AdStatus.PENDING_MODERATION, now)) {
            publishEvent(new AdSubmittedForModerationEvent(getId(), previousStatus, now));
        }
    }

    public void approve(Instant now) {
        assertNotDeleted();
        Objects.requireNonNull(now, "now is required");
        if (transitionTo(AdStatus.ACTIVE, now)) {
            expiresAt = now.plus(LIFETIME);
            publishEvent(new AdActivatedEvent(getId(), expiresAt, now));
        }
    }

    public void reject(String reason, Instant now) {
        assertNotDeleted();
        Objects.requireNonNull(now, "now is required");
        if (reason == null || reason.isBlank()) {
            throw new AdDomainException.FieldIsRequired("rejectionReason");
        }
        if (transitionTo(AdStatus.REJECTED, now)) {
            rejectionReason = reason;
            rejectedAt = now;
            publishEvent(new AdRejectedEvent(getId(), reason, now));
        }
    }

    public void suspend(String reason, Instant now) {
        assertNotDeleted();
        Objects.requireNonNull(now, "now is required");
        if (reason == null || reason.isBlank()) {
            throw new AdDomainException.FieldIsRequired("suspensionReason");
        }
        if (transitionTo(AdStatus.SUSPENDED, now)) {
            suspensionReason = reason;
            suspendedAt = now;
            publishEvent(new AdSuspendedEvent(getId(), reason, now));
        }
    }

    public void expire(Instant now) {
        assertNotDeleted();
        Objects.requireNonNull(now, "now is required");
        if (transitionTo(AdStatus.EXPIRED, now)) {
            publishEvent(new AdExpiredEvent(getId(), now));
        }
    }

    public void delete(Instant now) {
        Objects.requireNonNull(now, "now is required");

        if (transitionTo(AdStatus.DELETED, now)) {
            deletedAt = now;
            publishEvent(new AdDeletedEvent(getId(), now));
        }
    }

    public void changePrice(Mony newPrice, Instant now) {
        assertNotDeleted();
        ensureAssignedId();
        Objects.requireNonNull(newPrice, "newPrice is required");
        Objects.requireNonNull(now, "now is required");

        if (price.equals(newPrice)) {
            return;
        }

        price = newPrice;
        updatedAt = now;
        publishEvent(new AdPriceChangedEvent(getId(), price, now));
    }

    public void changeTitle(String newTitle, Instant now) {
        assertNotDeleted();
        ensureAssignedId();
        Objects.requireNonNull(now, "now is required");

        Title newTitleVo = new Title(newTitle == null ? null : newTitle.trim());
        if (title.equals(newTitleVo)) {
            return;
        }

        title = newTitleVo;
        updatedAt = now;
        publishEvent(new AdTitleChangedEvent(getId(), title.value(), now));
    }

    public void changeDetails(String newTitle, String newDescription, Instant now) {
        assertNotDeleted();
        ensureAssignedId();
        Objects.requireNonNull(now, "now is required");

        Title newTitleVo = new Title(newTitle == null ? null : newTitle.trim());
        Description newDescVo = newDescription == null ? null : new Description(newDescription);

        if (title.equals(newTitleVo) && Objects.equals(description, newDescVo)) {
            return;
        }

        title = newTitleVo;
        description = newDescVo;
        updatedAt = now;

        publishEvent(new AdUpdatedEvent(getId(), title.value(), description != null ? description.value() : null, now));
    }

    private void ensureAssignedId() {
        if (!getId().isAssigned()) {
            throw new AdDomainException.IdMustBeAssigned("id");
        }
    }

    private void assertNotDeleted() {
        if (isDeleted()) {
            throw new AdDomainException.DeletedAdModificationNotAllowed();
        }
    }

    private boolean transitionTo(AdStatus target, Instant now) {
        if (status == target) {
            return false;
        }
        if (!status.mayBecome(target)) {
            throw new AdDomainException.InvalidStatus(status.name(), target.name());
        }
        status = target;
        updatedAt = now;
        return true;
    }

    // ------------ Getters ------------

    @Override
    public AdId getId() {
        AdId id = super.getId();
        return id != null ? id : AdId.unassigned();
    }

    public OwnerId getOwnerId() {
        return ownerId;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }

    public AdLocation getLocation() {
        return location;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Title getTitle() {
        return title;
    }

    public Description getDescription() {
        return description;
    }

    public Mony getPrice() {
        return price;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public String getSuspensionReason() {
        return suspensionReason;
    }

    public AdStatus getStatus() {
        return status;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getRejectedAt() {
        return rejectedAt;
    }

    public Instant getSuspendedAt() {
        return suspendedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public boolean isDraft() {
        return status == AdStatus.DRAFT;
    }

    public boolean isPendingModeration() {
        return status == AdStatus.PENDING_MODERATION;
    }

    public boolean isActive() {
        return status == AdStatus.ACTIVE;
    }

    public boolean isExpired() {
        return status == AdStatus.EXPIRED;
    }

    public boolean isDeleted() {
        return status == AdStatus.DELETED;
    }

    public boolean isRejected() {
        return status == AdStatus.REJECTED;
    }

    public boolean isSuspended() {
        return status == AdStatus.SUSPENDED;
    }

}
