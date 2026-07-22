package com.nextbuy.adhub.ad.integration.infrastructure.persistence.repository;


import com.nextbuy.adhub.ad.domain.model.Ad;
import com.nextbuy.adhub.ad.domain.model.AdId;
import com.nextbuy.adhub.ad.domain.model.AdStatus;
import com.nextbuy.adhub.ad.domain.repository.AdRepository;
import com.nextbuy.adhub.ad.infrastructure.persistence.mapper.AdLocationPersistenceMapper;
import com.nextbuy.adhub.ad.infrastructure.persistence.mapper.AdPersistenceMapperImpl;
import com.nextbuy.adhub.ad.infrastructure.persistence.repository.AdRepositoryImpl;
import com.nextbuy.adhub.support.JpaRepositoryIntegrationTest;
import com.nextbuy.adhub.support.ad.fixtures.Ads;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Tags({@Tag("integration"), @Tag("repository")})
@Import({
        AdRepositoryImpl.class,
        AdPersistenceMapperImpl.class,
        AdLocationPersistenceMapper.class
})
@DisplayName("integration:AdRepositoryImplIT")
public class AdRepositoryImplIT extends JpaRepositoryIntegrationTest {
    @Autowired
    private AdRepository adRepository;

    @Test
    @DisplayName("It should insert a draft ad and reload it with location.")
    void saveNew_findById_roundTripsDraft() {
        var draft = Ads.draftForDb();

        AdId adId = adRepository.saveNew(draft);
        draft.assignId(adId);

        assertThat(adRepository.findById(adId))
                .isPresent()
                .get()
                .satisfies(found -> {
                    assertThat(found.getId()).isEqualTo(adId);
                    assertThat(found.getTitle().value()).isEqualTo(draft.getTitle().value());
                    assertThat(found.getDescription().value()).isEqualTo(draft.getDescription().value());
                    assertThat(found.getStatus()).isEqualTo(AdStatus.DRAFT);
                    assertThat(found.getLocation().cityId()).isEqualTo(draft.getLocation().cityId());
                    assertThat(found.getCreatedAt()).isNotNull();
                    assertThat(found.getUpdatedAt()).isNotNull();
                    assertThat(found.getExpiresAt()).isNull();
                    assertThat(found.getRejectedAt()).isNull();
                    assertThat(found.getDeletedAt()).isNull();
                });
    }

    @Test
    @DisplayName("It should update an existing ad.")
    void save_updatesPersistedAd() {
        AdId adId = adRepository.saveNew(Ads.draftForDb());
        Ad ad = adRepository.findById(adId).orElseThrow();
        ad.changeDetails("Updated title", ad.getDescription().value(), Instant.now());

        adRepository.save(ad);

        assertThat(adRepository.findById(adId))
                .get()
                .extracting(a -> a.getTitle().value())
                .isEqualTo("Updated title");
    }

    @Test
    @DisplayName("It should return empty when the ad does not exist.")
    void findById_emptyWhenMissing() {
        assertThat(adRepository.findById(AdId.of(
                java.util.UUID.fromString("00000000-0000-0000-0000-000000000001"))))
                .isEmpty();
    }
}
