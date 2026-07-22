package com.nextbuy.adhub.ad.application.event;

import com.nextbuy.adhub.ad.api.event.AdIntegrationEvent;
import com.nextbuy.adhub.ad.api.event.AdSubmittedForModerationIntegrationEvent;
import com.nextbuy.adhub.ad.domain.event.AdDomainEvent;
import com.nextbuy.adhub.ad.domain.event.AdSubmittedForModerationEvent;
import org.springframework.stereotype.Component;

@Component
public class AdEventMapper {

    public AdIntegrationEvent toIntegrationEvent(AdDomainEvent event) {
        if (event instanceof AdSubmittedForModerationEvent submittedEvent) {
            return toIntegrationEvent(submittedEvent);
        }
        throw new IllegalArgumentException("Unsupported domain event: " + event.getClass().getSimpleName());
    }

    public AdSubmittedForModerationIntegrationEvent toIntegrationEvent(AdSubmittedForModerationEvent event) {
        return new AdSubmittedForModerationIntegrationEvent(
                event.adId().valueOrThrow(),
                event.previousStatus().name(),
                event.occurredAt()
        );
    }
}
