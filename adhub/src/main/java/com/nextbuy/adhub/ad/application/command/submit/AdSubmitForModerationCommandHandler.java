package com.nextbuy.adhub.ad.application.command.submit;

import com.nextbuy.adhub.ad.application.event.AdEventMapper;
import com.nextbuy.adhub.ad.domain.event.AdDomainEvent;
import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.shared.exception.ValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdSubmitForModerationCommandHandler {

    private final AdRepository adRepository;
    private final AdEventMapper adEventMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AdSubmitForModerationResult handle(AdSubmitForModerationCommand command) {
        Ad ad = adRepository.findById(AdId.of(command.adId()))
                .orElseThrow(() -> new EntityNotFoundException("Ad"));

        if (!ad.getOwnerId().valueOrThrow().equals(command.ownerId())) {
            throw new ValidationException.AdOwnershipMismatch();
        }

        Instant now = Instant.now();
        ad.submitForModeration(now);
        adRepository.save(ad);

        for (AdDomainEvent event : ad.pullDomainEvents()) {
            var integrationEvent = adEventMapper.toIntegrationEvent(event);
            eventPublisher.publishEvent(integrationEvent);
        }

        return new AdSubmitForModerationResult(
                ad.getId().valueOrThrow(),
                ad.getStatus().name(),
                ad.getUpdatedAt()
        );
    }
}
