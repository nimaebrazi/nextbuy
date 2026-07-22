package com.nextbuy.adhub.location.application.command.importdata;

import com.nextbuy.adhub.location.domain.model.*;
import com.nextbuy.adhub.location.domain.repository.CityRepository;
import com.nextbuy.adhub.location.domain.repository.CountryRepository;
import com.nextbuy.adhub.location.domain.repository.ProvinceRepository;
import com.nextbuy.adhub.location.infrastructure.location.importdata.LocationSampleJsonReader;
import com.nextbuy.adhub.location.infrastructure.location.importdata.LocationSampleNode;
import com.nextbuy.adhub.location.infrastructure.location.importdata.PersianSlugTransliterator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ImportLocationHierarchyCommandHandler {

    private static final String IRAN_ISO_CODE = "IR";

    private final LocationSampleJsonReader jsonReader;
    private final PersianSlugTransliterator transliterator;
    private final CountryRepository countryRepository;
    private final ProvinceRepository provinceRepository;
    private final CityRepository cityRepository;

    public ImportLocationHierarchyCommandHandler(
            LocationSampleJsonReader jsonReader,
            PersianSlugTransliterator transliterator,
            CountryRepository countryRepository,
            ProvinceRepository provinceRepository,
            CityRepository cityRepository
    ) {
        this.jsonReader = jsonReader;
        this.transliterator = transliterator;
        this.countryRepository = countryRepository;
        this.provinceRepository = provinceRepository;
        this.cityRepository = cityRepository;
    }

    @Transactional
    public ImportLocationHierarchyResult handle(ImportLocationHierarchyCommand command) throws IOException {
        LocationSampleNode root = jsonReader.read(command.filePath()).root();

        int countriesCreated = 0;
        int provincesCreated = 0;
        int citiesCreated = 0;
        int countriesSkipped = 0;
        int provincesSkipped = 0;
        int citiesSkipped = 0;

        String countrySlug = transliterator.transliterate(root.name());
        CountryId countryId;
        var existingCountry = countryRepository.findByIsoCode(IRAN_ISO_CODE);
        if (existingCountry.isPresent()) {
            countryId = existingCountry.get().getId();
            countriesSkipped++;
        } else if (command.dryRun()) {
            countryId = CountryId.unassigned();
            countriesCreated++;
        } else {
            Country savedCountry = countryRepository.save(
                    Country.create(root.name(), IRAN_ISO_CODE, countrySlug, null, null)
            );
            countryId = savedCountry.getId();
            countriesCreated++;
        }

        Set<String> usedCitySlugs = new HashSet<>();
        Map<String, ProvinceId> provinceIdsBySourceId = new HashMap<>();

        for (LocationSampleNode provinceNode : root.childrenOrEmpty()) {
            String provinceSlug = resolveProvinceSlug(provinceNode);
            ProvinceId provinceId;
            var existingProvince = countryId.isAssigned()
                    ? provinceRepository.findByCountryIdAndSlug(countryId, provinceSlug)
                    : java.util.Optional.<Province>empty();

            if (existingProvince.isPresent()) {
                provinceId = existingProvince.get().getId();
                provincesSkipped++;
            } else if (command.dryRun()) {
                provinceId = ProvinceId.unassigned();
                provincesCreated++;
            } else {
                Province savedProvince = provinceRepository.save(
                        Province.create(provinceNode.name(), provinceSlug, countryId, null, null)
                );
                provinceId = savedProvince.getId();
                provincesCreated++;
            }

            provinceIdsBySourceId.put(provinceNode.sourceId(), provinceId);

            for (LocationSampleNode cityNode : provinceNode.childrenOrEmpty()) {
                String citySlug = resolveCitySlug(cityNode.name(), provinceSlug, usedCitySlugs);
                usedCitySlugs.add(citySlug);

                var existingCity = cityRepository.findBySlug(citySlug);
                if (existingCity.isPresent()) {
                    citiesSkipped++;
                    continue;
                }

                if (command.dryRun()) {
                    citiesCreated++;
                    continue;
                }

                cityRepository.save(
                        City.create(cityNode.name(), citySlug, provinceId, null, null)
                );
                citiesCreated++;
            }
        }

        return new ImportLocationHierarchyResult(
                countriesCreated,
                provincesCreated,
                citiesCreated,
                countriesSkipped,
                provincesSkipped,
                citiesSkipped
        );
    }

    private String resolveProvinceSlug(LocationSampleNode provinceNode) {
        String baseSlug = transliterator.transliterate(provinceNode.name());
        boolean hasSameNamedCity = provinceNode.childrenOrEmpty().stream()
                .anyMatch(child -> child.name().equals(provinceNode.name()));
        if (hasSameNamedCity) {
            return baseSlug + "-province";
        }
        return baseSlug;
    }

    private String resolveCitySlug(String cityName, String provinceSlug, Set<String> usedCitySlugs) {
        String baseSlug = transliterator.transliterate(cityName);
        String provinceFragment = provinceSlug.endsWith("-province")
                ? provinceSlug.substring(0, provinceSlug.length() - "-province".length())
                : provinceSlug;

        String candidate = baseSlug;
        if (usedCitySlugs.contains(candidate)) {
            candidate = baseSlug + "-" + provinceFragment;
        }

        int counter = 2;
        while (usedCitySlugs.contains(candidate)) {
            candidate = baseSlug + "-" + provinceFragment + "-" + counter++;
        }

        return candidate;
    }
}
