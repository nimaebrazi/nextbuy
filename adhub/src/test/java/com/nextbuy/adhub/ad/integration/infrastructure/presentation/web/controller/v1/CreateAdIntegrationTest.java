package com.nextbuy.adhub.ad.integration.infrastructure.presentation.web.controller.v1;

import com.nextbuy.adhub.support.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/ad/create-ad-fixtures.sql")
@DisplayName("integration:CreateAdIntegrationTest")
class CreateAdIntegrationTest {

    private static final String CATEGORY_ID = "11111111-1111-1111-1111-111111111111";
    private static final String COUNTRY_ID = "22222222-2222-2222-2222-222222222222";
    private static final String PROVINCE_ID = "33333333-3333-3333-3333-333333333333";
    private static final String CITY_ID = "44444444-4444-4444-4444-444444444444";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("It should persist a draft ad without publishing an integration event.")
    void createAd_persistsAdWithoutEventPublication() throws Exception {
        mockMvc.perform(post("/api/v1/ads")
                        .header("X-Owner-Id", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Test Ad",
                                  "description": "Integration test ad",
                                  "priceAmount": 99.99,
                                  "priceCurrency": "USD",
                                  "categoryId": "%s",
                                  "countryId": "%s",
                                  "provinceId": "%s",
                                  "cityId": "%s"
                                }
                                """.formatted(CATEGORY_ID, COUNTRY_ID, PROVINCE_ID, CITY_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.id").exists());

        Integer adCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ads WHERE owner_id = ? AND title = ?",
                Integer.class,
                42L,
                "Test Ad"
        );
        assertThat(adCount).isEqualTo(1);

        Integer locationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ad_locations WHERE country_id = ?::uuid AND city_id = ?::uuid",
                Integer.class,
                COUNTRY_ID,
                CITY_ID
        );
        assertThat(locationCount).isEqualTo(1);

        Integer publicationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_publication",
                Integer.class
        );
        assertThat(publicationCount).isEqualTo(0);
    }

    @Test
    @DisplayName("It should publish AdSubmittedForModerationIntegrationEvent when a draft is submitted.")
    void submitForModeration_persistsStatusAndEventPublication() throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1/ads")
                        .header("X-Owner-Id", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Submit Me",
                                  "description": "Ready for review",
                                  "priceAmount": 50.00,
                                  "priceCurrency": "USD",
                                  "categoryId": "%s",
                                  "countryId": "%s",
                                  "provinceId": "%s",
                                  "cityId": "%s"
                                }
                                """.formatted(CATEGORY_ID, COUNTRY_ID, PROVINCE_ID, CITY_ID)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String adId = JsonPath.read(createResponse, "$.data.id");

        mockMvc.perform(post("/api/v1/ads/{adId}/submit", adId)
                        .header("X-Owner-Id", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING_MODERATION"))
                .andExpect(jsonPath("$.data.adId").value(adId));

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM ads WHERE id = ?::uuid",
                String.class,
                adId
        );
        assertThat(status).isEqualTo("PENDING_MODERATION");

        Integer publicationCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM event_publication WHERE event_type LIKE '%AdSubmittedForModerationIntegrationEvent'",
                Integer.class
        );
        assertThat(publicationCount).isEqualTo(1);
    }
}
