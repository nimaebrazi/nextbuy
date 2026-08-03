package com.nextbuy.adhub.ad.application.command.approve;


import com.nextbuy.adhub.ad.application.event.AdDomainEventPublisher;
import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdApproveCommandHandler {

    private final AdRepository adRepository;
    private final AdDomainEventPublisher eventPublisher;

    @Transactional
    public AdApproveResult handle(AdApproveCommand command){

        Ad ad = adRepository.findById(AdId.of(command.adId()))
                .orElseThrow(() -> new EntityNotFoundException("Ad"));

        ad.approve(Instant.now());
        adRepository.save(ad);
        eventPublisher.publish(ad);

        return new AdApproveResult(
                ad.getId().valueOrThrow(),
                ad.getStatus().name(),
                ad.getUpdatedAt()
        );
    }

}
