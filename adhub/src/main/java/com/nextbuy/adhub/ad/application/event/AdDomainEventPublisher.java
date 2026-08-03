package com.nextbuy.adhub.ad.application.event;

import com.nextbuy.adhub.ad.domain.event.AdDomainEvent;
import com.nextbuy.adhub.ad.domain.model.Ad;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdDomainEventPublisher {

    private final AdEventMapper adEventMapper;
    private final ApplicationEventPublisher eventPublisher;

    public void publish(Ad ad) {
        for (AdDomainEvent event : ad.pullDomainEvents()) {
            eventPublisher.publishEvent(adEventMapper.toIntegrationEvent(event));
        }
    }
}
