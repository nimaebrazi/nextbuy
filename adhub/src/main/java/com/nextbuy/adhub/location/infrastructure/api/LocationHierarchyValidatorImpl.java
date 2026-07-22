package com.nextbuy.adhub.location.infrastructure.api;

import com.nextbuy.adhub.location.api.LocationHierarchyValidator;
import com.nextbuy.adhub.location.api.LocationSelection;
import com.nextbuy.adhub.location.api.LocationValidationException;
import com.nextbuy.adhub.location.api.ValidatedLocation;
import com.nextbuy.adhub.location.domain.model.CityId;
import com.nextbuy.adhub.location.domain.model.CountryId;
import com.nextbuy.adhub.location.domain.model.GeoPoint;
import com.nextbuy.adhub.location.domain.model.Neighbourhood;
import com.nextbuy.adhub.location.domain.model.NeighbourhoodId;
import com.nextbuy.adhub.location.domain.model.ProvinceId;
import com.nextbuy.adhub.location.domain.repository.CityRepository;
import com.nextbuy.adhub.location.domain.repository.CountryRepository;
import com.nextbuy.adhub.location.domain.repository.NeighbourhoodRepository;
import com.nextbuy.adhub.location.domain.repository.ProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationHierarchyValidatorImpl implements LocationHierarchyValidator {

    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;

    /**
     * Validates hierarchy consistency (province ∈ country, city ∈ province,
     * neighbourhood ∈ city) and — when both a neighbourhood and an exact pin are
     * given — that the pin falls inside the neighbourhood's boundary.
     */
    @Override
    public ValidatedLocation validate(LocationSelection selection) {
        var countryId = CountryId.of(selection.countryId());
        var provinceId = ProvinceId.of(selection.provinceId());
        var cityId = CityId.of(selection.cityId());
        var neighbourhoodId = selection.neighbourhoodId() != null
                ? NeighbourhoodId.of(selection.neighbourhoodId()) : null;
        var point = GeoPoint.ofNullable(selection.latitude(), selection.longitude());

        if (!countryRepository.existsById(countryId)) {
            throw new LocationValidationException("countryId", "Country does not exist");
        }

        if (!provinceRepository.existsByIdAndCountryId(provinceId, countryId)) {
            throw new LocationValidationException(
                    "provinceId", "Province does not exist in the given country"
            );
        }

        if (!cityRepository.existsByIdAndProvinceId(cityId, provinceId)) {
            throw new LocationValidationException(
                    "cityId", "City does not exist in the given province"
            );
        }

        if (neighbourhoodId != null) {
            Neighbourhood neighbourhood = neighbourhoodRepository.findById(neighbourhoodId)
                    .filter(n -> n.getCityId().equals(cityId))
                    .orElseThrow(() -> new LocationValidationException(
                            "neighbourhoodId", "Neighbourhood does not exist in the given city")
                    );

            if (point != null && neighbourhood.getBoundary() != null && !neighbourhood.contains(point)) {
                throw new LocationValidationException(
                        "location", "Point must be inside the selected neighbourhood"
                );
            }
        }

        return new ValidatedLocation(
                selection.countryId(),
                selection.provinceId(),
                selection.cityId(),
                selection.neighbourhoodId(),
                selection.latitude(),
                selection.longitude()
        );
    }
}
