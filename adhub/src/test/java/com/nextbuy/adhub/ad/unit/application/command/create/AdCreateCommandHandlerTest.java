package com.nextbuy.adhub.ad.unit.application.command.create;

import com.nextbuy.adhub.ad.application.command.create.AdCreateCommand;
import com.nextbuy.adhub.ad.application.command.create.AdCreateCommandHandler;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.category.api.CategoryExistenceChecker;
import com.nextbuy.adhub.location.api.LocationHierarchyValidator;
import com.nextbuy.adhub.location.api.ValidatedLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("application:command:create:AdCreateCommandHandlerTest")
class AdCreateCommandHandlerTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private CategoryExistenceChecker categoryExistenceChecker;
    @Mock
    private LocationHierarchyValidator locationHierarchyValidator;

    @InjectMocks
    private AdCreateCommandHandler handler;

    @Test
    @DisplayName("It should create a draft ad when references are valid.")
    void should_CreateDraftAd_When_ReferencesValid() {
        UUID categoryId = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();
        UUID provinceId = UUID.randomUUID();
        UUID cityId = UUID.randomUUID();
        UUID adId = UUID.randomUUID();

        when(categoryExistenceChecker.exists(categoryId)).thenReturn(true);
        when(locationHierarchyValidator.validate(any())).thenReturn(
                new ValidatedLocation(countryId, provinceId, cityId, null, null, null)
        );
        when(adRepository.saveNew(any())).thenReturn(AdId.of(adId));

        var command = new AdCreateCommand(
                42L,
                "Test ad",
                "Description",
                BigDecimal.TEN,
                "USD",
                categoryId,
                countryId,
                provinceId,
                cityId,
                null,
                null,
                null
        );

        var result = handler.handle(command);

        assertThat(result.id()).isEqualTo(adId);
        assertThat(result.status()).isEqualTo("DRAFT");
        verify(adRepository).saveNew(any());
    }
}
