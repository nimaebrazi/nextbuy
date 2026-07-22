package com.nextbuy.adhub.ad.application.command.create;

import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdLocation;
import com.nextbuy.adhub.ad.domain.model.CategoryId;
import com.nextbuy.adhub.ad.domain.model.OwnerId;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.category.api.CategoryExistenceChecker;
import com.nextbuy.adhub.location.api.LocationHierarchyValidator;
import com.nextbuy.adhub.location.api.LocationSelection;
import com.nextbuy.adhub.location.api.LocationValidationException;
import com.nextbuy.adhub.shared.domain.Mony;
import com.nextbuy.adhub.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdCreateCommandHandler {

    private final AdRepository adRepository;
    private final CategoryExistenceChecker categoryExistenceChecker;
    private final LocationHierarchyValidator locationHierarchyValidator;

    @Transactional
    public AdCreatedResult handle(AdCreateCommand command) {

        if (!categoryExistenceChecker.exists(command.categoryId())) {
            throw new ValidationException.InvalidCategoryReference();
        }

        AdLocation location = resolveLocation(command);

        Ad ad = Ad.createDraft(
                OwnerId.of(command.ownerId()),
                CategoryId.of(command.categoryId()),
                command.title(),
                command.description(),
                new Mony(command.price(), command.currency()),
                location,
                Instant.now()
        );

        AdId id = adRepository.saveNew(ad);
        ad.assignId(id);

        return new AdCreatedResult(
                ad.getId().valueOrThrow(),
                ad.getStatus().name(),
                ad.getCreatedAt()
        );
    }

    private AdLocation resolveLocation(AdCreateCommand command) {
        try {
            var validated = locationHierarchyValidator.validate(new LocationSelection(
                    command.countryId(),
                    command.provinceId(),
                    command.cityId(),
                    command.neighbourhoodId(),
                    command.latitude(),
                    command.longitude()
            ));
            return AdLocation.of(
                    validated.countryId(),
                    validated.provinceId(),
                    validated.cityId(),
                    validated.neighbourhoodId(),
                    validated.latitude(),
                    validated.longitude()
            );
        } catch (LocationValidationException ex) {
            throw new ValidationException.InvalidLocationReference(ex.getField(), ex.reason());
        }
    }
}
