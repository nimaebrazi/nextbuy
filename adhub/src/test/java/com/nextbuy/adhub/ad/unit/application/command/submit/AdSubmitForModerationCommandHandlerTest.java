package com.nextbuy.adhub.ad.unit.application.command.submit;

import com.nextbuy.adhub.ad.api.event.AdSubmittedForModerationIntegrationEvent;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommand;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommandHandler;
import com.nextbuy.adhub.ad.application.event.AdEventMapper;
import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdStatus;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.shared.exception.ValidationException;
import com.nextbuy.adhub.support.ad.fixtures.Ads;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("application:command:submit:AdSubmitForModerationCommandHandlerTest")
class AdSubmitForModerationCommandHandlerTest {

    @Mock
    private AdRepository adRepository;
    @Spy
    private AdEventMapper adEventMapper = new AdEventMapper();
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AdSubmitForModerationCommandHandler handler;

    @Test
    @DisplayName("It should submit a draft for moderation and publish an integration event.")
    void should_SubmitDraftAndPublishEvent_When_OwnerMatches() {
        Ad ad = Ads.withPersistedId();
        long ownerId = ad.getOwnerId().valueOrThrow();
        UUID adId = ad.getId().valueOrThrow();

        when(adRepository.findById(AdId.of(adId))).thenReturn(Optional.of(ad));

        var result = handler.handle(new AdSubmitForModerationCommand(adId, ownerId));

        assertThat(result.adId()).isEqualTo(adId);
        assertThat(result.status()).isEqualTo(AdStatus.PENDING_MODERATION.name());
        verify(adRepository).save(ad);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(AdSubmittedForModerationIntegrationEvent.class);
        var published = (AdSubmittedForModerationIntegrationEvent) eventCaptor.getValue();
        assertThat(published.adId()).isEqualTo(adId);
        assertThat(published.previousStatus()).isEqualTo(AdStatus.DRAFT.name());
    }

    @Test
    @DisplayName("It should reject submission when the ad does not exist.")
    void should_Throw_When_AdNotFound() {
        UUID adId = UUID.randomUUID();
        when(adRepository.findById(AdId.of(adId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> handler.handle(new AdSubmitForModerationCommand(adId, 42L)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Ad");
    }

    @Test
    @DisplayName("It should reject submission when the caller is not the owner.")
    void should_Throw_When_OwnerMismatch() {
        Ad ad = Ads.withPersistedId();
        UUID adId = ad.getId().valueOrThrow();
        when(adRepository.findById(AdId.of(adId))).thenReturn(Optional.of(ad));

        assertThatThrownBy(() -> handler.handle(new AdSubmitForModerationCommand(adId, ownerIdOtherThan(ad))))
                .isInstanceOf(ValidationException.AdOwnershipMismatch.class);
    }

    private static long ownerIdOtherThan(Ad ad) {
        long ownerId = ad.getOwnerId().valueOrThrow();
        return ownerId == Long.MAX_VALUE ? ownerId - 1 : ownerId + 1;
    }
}
