package com.nextbuy.adhub.ad.unit.domain.model;

import com.nextbuy.adhub.ad.domain.event.*;
import com.nextbuy.adhub.ad.domain.exception.AdDomainException;
import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdStatus;
import com.nextbuy.adhub.shared.domain.Mony;
import com.nextbuy.adhub.support.ad.fixtures.Ads;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;

import static com.nextbuy.adhub.support.ad.fixtures.Ads.TEST_AD_ID;
import static com.nextbuy.adhub.support.ad.fixtures.Ads.TEST_AD_ID_2;
import static com.nextbuy.adhub.support.shared.Fakers.faker;
import static org.assertj.core.api.Assertions.*;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:AdTest")
public class AdTest {

    private static final Instant NOW = Ads.NOW;

    @Test
    @DisplayName("It should create a draft ad with a trimmed title and default lifecycle fields when all inputs are valid.")
    void createDraft_CreateDraftAd_When_AllFieldsValid() {
        Ad ad = Ads.withDetails("  iPhone 15  ", "Like new");

        assertThat(ad.getId().isAssigned()).isFalse();
        assertThat(ad.getStatus()).isEqualTo(AdStatus.DRAFT);
        assertThat(ad.getTitle().value()).isEqualTo("iPhone 15");
        assertThat(ad.getDescription().value()).isEqualTo("Like new");
        assertThat(ad.getOwnerId()).isNotNull();
        assertThat(ad.getCategoryId()).isNotNull();
        assertThat(ad.getLocation()).isNotNull();
        assertThat(ad.getPrice()).isNotNull();
        assertThat(ad.getCreatedAt()).isNotNull();
        assertThat(ad.getUpdatedAt()).isEqualTo(ad.getCreatedAt());
        assertThat(ad.getExpiresAt()).isCloseTo(
                ad.getCreatedAt().plus(30, ChronoUnit.DAYS),
                within(1, ChronoUnit.SECONDS)
        );
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should reject creation when the title is blank.")
    void createDraft_Throw_When_TitleIsBlank() {
        assertThatThrownBy(() -> Ads.withTitle("  "))
                .isInstanceOf(AdDomainException.FieldIsRequired.class)
                .hasMessage("Ad 'title' field is required!");
    }

    @Test
    @DisplayName("It should reject creation when the title is missing.")
    void createDraft_Throw_When_TitleIsNull() {
        assertThatThrownBy(() -> Ads.withTitle(null))
                .isInstanceOf(AdDomainException.FieldIsRequired.class)
                .hasMessage("Ad 'title' field is required!");
    }

    @Test
    @DisplayName("It should reject creation when the title is longer than 200 characters.")
    void createDraft_Throw_When_TitleExceedsMaxLength() {
        String tooLongTitle = faker().lorem().characters(201);

        assertThatThrownBy(() -> Ads.withTitle(tooLongTitle))
                .isInstanceOf(AdDomainException.ExceededLimitLength.class)
                .hasMessage("Field title exceeds maximum length. Limit is 200!");
    }

    @Test
    @DisplayName("It should reject creation when the description is longer than 5000 characters.")
    void createDraft_Throw_When_DescriptionExceedsMaxLength() {
        String tooLongDescription = faker().lorem().characters(5001);

        assertThatThrownBy(() -> Ads.withDetails("iPhone 15", tooLongDescription))
                .isInstanceOf(AdDomainException.ExceededLimitLength.class)
                .hasMessage("Field description exceeds maximum length. Limit is 5000!");
    }

    @Test
    @DisplayName("It should reject creation when the owner is missing.")
    void createDraft_Throw_When_OwnerIdMissing() {
        assertThatThrownBy(Ads::withoutOwnerId)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ownerId is required");
    }

    @Test
    @DisplayName("It should reject creation when the category is missing.")
    void createDraft_Throw_When_CategoryIdMissing() {
        assertThatThrownBy(Ads::withUnassignedCategoryId)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("categoryId is required");
    }

    @Test
    @DisplayName("It should reject creation when the location is missing.")
    void createDraft_Throw_When_LocationMissing() {
        assertThatThrownBy(Ads::withNullLocation)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("location is required");
    }

    @Test
    @DisplayName("It should reject creation when the price is missing.")
    void createDraft_Throw_When_PriceIsNull() {
        assertThatThrownBy(Ads::withNullPrice)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("price is required");
    }

    @Test
    @DisplayName("It should not raise domain events when an id is assigned after persist.")
    void assignId_NotRaiseEvent_When_IdAssigned() {
        Ad ad = Ads.randomDraft();
        AdId assignedId = AdId.of(TEST_AD_ID);

        ad.assignId(assignedId);

        assertThat(ad.getId()).isEqualTo(assignedId);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should not raise domain events when only create is called.")
    void createDraft_NotRaiseEvent_When_OnlyCreateCalled() {
        assertThat(Ads.randomDraft().pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should reject assigning an id when the ad already has one.")
    void assignId_Throw_When_IdAlreadyAssigned() {
        Ad ad = Ads.withId(TEST_AD_ID);
        assertThatThrownBy(() -> ad.assignId(AdId.of(TEST_AD_ID_2)))
                .isInstanceOf(AdDomainException.class)
                .hasMessage("Id is already assigned");
    }

    @Test
    @DisplayName("It should reject assigning an id that has not been assigned yet.")
    void assignId_Throw_When_AssignedIdIsUnassigned() {
        Ad ad = Ads.randomDraft();
        assertThatThrownBy(() -> ad.assignId(AdId.unassigned()))
                .isInstanceOf(AdDomainException.AssignedIdMustBeAssigned.class)
                .hasMessage("Assigned id must be assigned");
    }

    @Test
    @DisplayName("It should clear domain events after they have been pulled.")
    void pullDomainEvents_ClearEvents_When_CalledTwice() {
        Ad ad = Ads.withPersistedId(TEST_AD_ID);
        ad.submitForModeration(NOW);
        assertThat(ad.pullDomainEvents()).hasSize(1);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should submit a draft for moderation and raise a submitted event.")
    void submitForModeration_RaiseAdSubmittedForModerationEvent_When_DraftSubmitted() {
        Ad ad = Ads.withPersistedId();

        ad.submitForModeration(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertThat(ad.getUpdatedAt()).isEqualTo(NOW);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdSubmittedForModerationEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.previousStatus()).isEqualTo(AdStatus.DRAFT);
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should resubmit a rejected ad for moderation.")
    void submitForModeration_RaiseAdSubmittedForModerationEvent_When_RejectedResubmitted() {
        Ad ad = Ads.rejected("Inappropriate content");

        ad.submitForModeration(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdSubmittedForModerationEvent.class,
                event -> assertThat(event.previousStatus()).isEqualTo(AdStatus.REJECTED)
        );
    }

    @Test
    @DisplayName("It should renew an expired ad by submitting it for moderation.")
    void submitForModeration_RaiseAdSubmittedForModerationEvent_When_ExpiredRenewed() {
        Ad ad = Ads.expired();

        ad.submitForModeration(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdSubmittedForModerationEvent.class,
                event -> assertThat(event.previousStatus()).isEqualTo(AdStatus.EXPIRED)
        );
    }

    @Test
    @DisplayName("It should allow a suspended ad to be edited and sent for review.")
    void submitForModeration_RaiseAdSubmittedForModerationEvent_When_SuspendedResubmitted() {
        Ad ad = Ads.suspended("Policy violation");

        ad.submitForModeration(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdSubmittedForModerationEvent.class,
                event -> assertThat(event.previousStatus()).isEqualTo(AdStatus.SUSPENDED)
        );
    }

    @Test
    @DisplayName("It should not raise another event when submitting an ad already pending moderation.")
    void submitForModeration_BeIdempotent_When_AlreadyPendingModeration() {
        Ad ad = Ads.pendingModeration();

        ad.submitForModeration(NOW.plusSeconds(60));

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should refuse to submit an active ad for moderation without a sensitive edit.")
    void submitForModeration_Throw_When_AdIsActive() {
        Ad ad = Ads.active();

        assertThatThrownBy(() -> ad.submitForModeration(NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should apply a sensitive edit on an active ad and send it back to moderation.")
    void submitSensitiveEdit_RaiseEvents_When_ActiveAdEdited() {
        Ad ad = Ads.active();

        ad.submitSensitiveEdit("  New title  ", "New description", NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertThat(ad.getTitle().value()).isEqualTo("New title");
        assertThat(ad.getDescription().value()).isEqualTo("New description");

        List<AdDomainEvent> events = ad.pullDomainEvents();
        assertThat(events).hasSize(2);
        assertThat(events.get(0)).isInstanceOfSatisfying(AdUpdatedEvent.class, event -> {
            assertThat(event.title()).isEqualTo("New title");
            assertThat(event.description()).isEqualTo("New description");
        });
        assertThat(events.get(1)).isInstanceOfSatisfying(AdSubmittedForModerationEvent.class, event ->
                assertThat(event.previousStatus()).isEqualTo(AdStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("It should send an active ad to moderation on sensitive edit even when details are unchanged.")
    void submitSensitiveEdit_RaiseSubmittedEventOnly_When_DetailsUnchanged() {
        Ad ad = Ads.active();
        String title = ad.getTitle().value();
        String description = ad.getDescription().value();

        ad.submitSensitiveEdit(title, description, NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.PENDING_MODERATION);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdSubmittedForModerationEvent.class,
                event -> assertThat(event.previousStatus()).isEqualTo(AdStatus.ACTIVE)
        );
    }

    @Test
    @DisplayName("It should refuse a sensitive edit when the ad is not active.")
    void submitSensitiveEdit_Throw_When_AdIsDraft() {
        Ad ad = Ads.withPersistedId();

        assertThatThrownBy(() -> ad.submitSensitiveEdit("Title", "Description", NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should approve a pending ad, restamp expiry, and raise an activation event.")
    void approve_RaiseAdActivatedEvent_When_PendingApproved() {
        Ad ad = Ads.pendingModeration();

        ad.approve(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.ACTIVE);
        assertThat(ad.getExpiresAt()).isEqualTo(NOW.plus(30, ChronoUnit.DAYS));
        assertThat(ad.getUpdatedAt()).isEqualTo(NOW);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdActivatedEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.expiresAt()).isEqualTo(ad.getExpiresAt());
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should not raise another event when approving an already active ad.")
    void approve_BeIdempotent_When_AlreadyActive() {
        Ad ad = Ads.active();

        ad.approve(NOW.plusSeconds(60));

        assertThat(ad.getStatus()).isEqualTo(AdStatus.ACTIVE);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should refuse to approve a draft ad that was never submitted.")
    void approve_Throw_When_AdIsDraft() {
        Ad ad = Ads.withPersistedId();

        assertThatThrownBy(() -> ad.approve(NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should refuse to approve a suspended ad without resubmission.")
    void approve_Throw_When_AdIsSuspended() {
        Ad ad = Ads.suspended("Violation");

        assertThatThrownBy(() -> ad.approve(NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should refuse to approve an ad that has already expired.")
    void approve_Throw_When_AdIsExpired() {
        Ad ad = Ads.expired();

        assertThatThrownBy(() -> ad.approve(NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should update the price and raise a price-changed event when the price changes.")
    void changePrice_RaiseAdPriceChangedEvent_When_PriceChanges() {
        Ad ad = Ads.withPersistedId();
        Mony newPrice = Ads.randomPrice();

        ad.changePrice(newPrice, NOW);
        assertThat(ad.getPrice()).isEqualTo(newPrice);

        assertSingleEvent(
                ad.pullDomainEvents(),
                AdPriceChangedEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.price()).isEqualTo(newPrice);
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should not raise an event when the price is unchanged.")
    void changePrice_NotRaiseEvent_When_PriceIsUnchanged() {
        Ad ad = Ads.withPersistedId();
        Mony samePrice = ad.getPrice();
        ad.changePrice(samePrice, NOW);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should update details and raise an update event with a trimmed title.")
    void changeDetails_RaiseAdUpdatedEvent_When_DetailsChange() {
        Ad ad = Ads.withPersistedId();
        ad.changeDetails("  New title  ", "New description", NOW);
        assertThat(ad.getTitle().value()).isEqualTo("New title");
        assertThat(ad.getDescription().value()).isEqualTo("New description");
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdUpdatedEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.title()).isEqualTo("New title");
                    assertThat(event.description()).isEqualTo("New description");
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should not raise an event when the details are unchanged.")
    void changeDetails_NotRaiseEvent_When_DetailsAreUnchanged() {
        Ad ad = Ads.withPersistedId();
        ad.changeDetails(ad.getTitle().value(), ad.getDescription().value(), NOW);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should expire an active ad and raise an expiry event.")
    void expire_RaiseAdExpiredEvent_When_ActiveAdExpires() {
        Ad ad = Ads.active();

        ad.expire(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.EXPIRED);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdExpiredEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should refuse to expire a draft ad.")
    void expire_Throw_When_AdIsDraft() {
        Ad ad = Ads.withPersistedId();
        assertThatThrownBy(() -> ad.expire(NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class)
                .hasMessageContaining("Ad status: DRAFT is not valid for status: EXPIRED!");
    }

    @Test
    @DisplayName("It should not raise another event when expiring an already expired ad.")
    void expire_BeIdempotent_When_AlreadyExpired() {
        Ad ad = Ads.expired();

        ad.expire(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.EXPIRED);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should mark an ad as deleted and raise a deletion event.")
    void delete_RaiseAdDeletedEvent_When_AdIsDeleted() {
        Ad ad = Ads.withPersistedId();
        ad.delete(NOW);
        assertThat(ad.getStatus()).isEqualTo(AdStatus.DELETED);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdDeletedEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should allow a suspended ad to be deleted when the owner abandons it.")
    void delete_RaiseAdDeletedEvent_When_SuspendedAdDeleted() {
        Ad ad = Ads.suspended("Violation");

        ad.delete(NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.DELETED);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdDeletedEvent.class,
                event -> assertThat(event.adId()).isEqualTo(ad.getId())
        );
    }

    @Test
    @DisplayName("It should not raise another event when deleting an already deleted ad.")
    void delete_BeIdempotent_When_AlreadyDeleted() {
        Ad ad = Ads.withPersistedId();
        ad.delete(NOW);
        ad.pullDomainEvents();
        ad.delete(NOW);
        assertThat(ad.getStatus()).isEqualTo(AdStatus.DELETED);
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should refuse to modify an ad that was already deleted.")
    void changePrice_Throw_When_AdIsDeleted() {
        Ad ad = Ads.withPersistedId();
        ad.delete(NOW);
        assertThatThrownBy(() -> ad.changePrice(Ads.randomPrice(), NOW))
                .isInstanceOf(AdDomainException.DeletedAdModificationNotAllowed.class)
                .hasMessage("Deleted ad cannot be modified.");
    }

    @Test
    @DisplayName("It should reject a pending ad and raise a rejection event.")
    void reject_RaiseAdRejectedEvent_When_PendingRejected() {
        Ad ad = Ads.pendingModeration();

        ad.reject("Inappropriate content", NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.REJECTED);
        assertThat(ad.getRejectionReason()).isEqualTo("Inappropriate content");
        assertThat(ad.getRejectedAt()).isEqualTo(NOW);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdRejectedEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.reason()).isEqualTo("Inappropriate content");
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should refuse to reject a draft that was never submitted.")
    void reject_Throw_When_AdIsDraft() {
        Ad ad = Ads.withPersistedId();

        assertThatThrownBy(() -> ad.reject("Inappropriate content", NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should refuse to reject an active ad without moderation.")
    void reject_Throw_When_AdIsActive() {
        Ad ad = Ads.active();

        assertThatThrownBy(() -> ad.reject("Policy violation", NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should not raise another event when rejecting an already rejected ad.")
    void reject_BeIdempotent_When_AlreadyRejected() {
        Ad ad = Ads.rejected("First reason");

        ad.reject("Second reason", NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.REJECTED);
        assertThat(ad.getRejectionReason()).isEqualTo("First reason");
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should refuse to reject an ad that has already expired.")
    void reject_Throw_When_AdIsExpired() {
        Ad ad = Ads.expired();

        assertThatThrownBy(() -> ad.reject("Too late", NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should refuse to reject an ad that was already deleted.")
    void reject_Throw_When_AdIsDeleted() {
        Ad ad = Ads.withPersistedId();
        ad.delete(NOW);

        assertThatThrownBy(() -> ad.reject("Too late", NOW))
                .isInstanceOf(AdDomainException.DeletedAdModificationNotAllowed.class);
    }

    @Test
    @DisplayName("It should refuse rejection when the reason is blank.")
    void reject_Throw_When_RejectionReasonIsBlank() {
        Ad ad = Ads.pendingModeration();

        assertThatThrownBy(() -> ad.reject("  ", NOW))
                .isInstanceOf(AdDomainException.FieldIsRequired.class)
                .hasMessage("Ad 'rejectionReason' field is required!");
    }

    @Test
    @DisplayName("It should suspend an active ad and raise a suspension event.")
    void suspend_RaiseAdSuspendedEvent_When_ActiveSuspended() {
        Ad ad = Ads.active();

        ad.suspend("Policy violation", NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.SUSPENDED);
        assertThat(ad.getSuspensionReason()).isEqualTo("Policy violation");
        assertThat(ad.getSuspendedAt()).isEqualTo(NOW);
        assertSingleEvent(
                ad.pullDomainEvents(),
                AdSuspendedEvent.class,
                event -> {
                    assertThat(event.adId()).isEqualTo(ad.getId());
                    assertThat(event.reason()).isEqualTo("Policy violation");
                    assertThat(event.occurredAt()).isEqualTo(NOW);
                }
        );
    }

    @Test
    @DisplayName("It should refuse to suspend a draft ad.")
    void suspend_Throw_When_AdIsDraft() {
        Ad ad = Ads.withPersistedId();

        assertThatThrownBy(() -> ad.suspend("Too early", NOW))
                .isInstanceOf(AdDomainException.InvalidStatus.class);
    }

    @Test
    @DisplayName("It should not raise another event when suspending an already suspended ad.")
    void suspend_BeIdempotent_When_AlreadySuspended() {
        Ad ad = Ads.suspended("First reason");

        ad.suspend("Second reason", NOW);

        assertThat(ad.getStatus()).isEqualTo(AdStatus.SUSPENDED);
        assertThat(ad.getSuspensionReason()).isEqualTo("First reason");
        assertThat(ad.pullDomainEvents()).isEmpty();
    }

    @Test
    @DisplayName("It should refuse suspension when the reason is blank.")
    void suspend_Throw_When_SuspensionReasonIsBlank() {
        Ad ad = Ads.active();

        assertThatThrownBy(() -> ad.suspend("  ", NOW))
                .isInstanceOf(AdDomainException.FieldIsRequired.class)
                .hasMessage("Ad 'suspensionReason' field is required!");
    }

    private static <T> void assertSingleEvent(
            List<AdDomainEvent> events,
            Class<T> eventType,
            Consumer<T> assertions) {

        assertThat(events)
                .singleElement()
                .isInstanceOfSatisfying(eventType, assertions);
    }
}
