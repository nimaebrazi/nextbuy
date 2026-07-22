package com.nextbuy.adhub.ad.unit.domain.model;

import com.nextbuy.adhub.ad.domain.model.AdStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tags({@Tag("model"), @Tag("unit")})
@DisplayName("domain:models:AdStatusTest")
class AdStatusTest {

    @ParameterizedTest
    @EnumSource(value = AdStatus.class, names = {"DRAFT", "REJECTED", "EXPIRED", "SUSPENDED", "PENDING_MODERATION"})
    @DisplayName("It should allow submit-for-moderation from draft, rejected, expired, suspended, and pending statuses.")
    void canSubmitForModeration_ReturnTrue_When_StatusAllowsSubmit(AdStatus status) {
        assertThat(status.canSubmitForModeration()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = AdStatus.class, names = {"ACTIVE", "DELETED"})
    @DisplayName("It should refuse submit-for-moderation from active and deleted statuses.")
    void canSubmitForModeration_ReturnFalse_When_StatusDoesNotAllowSubmit(AdStatus status) {
        assertThat(status.canSubmitForModeration()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = AdStatus.class, names = {"ACTIVE", "PENDING_MODERATION"})
    @DisplayName("It should allow sensitive edit from active and pending moderation statuses.")
    void canSubmitSensitiveEdit_ReturnTrue_When_StatusAllowsSensitiveEdit(AdStatus status) {
        assertThat(status.canSubmitSensitiveEdit()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = AdStatus.class, names = {"DRAFT", "REJECTED", "SUSPENDED", "EXPIRED", "DELETED"})
    @DisplayName("It should refuse sensitive edit from statuses other than active and pending moderation.")
    void canSubmitSensitiveEdit_ReturnFalse_When_StatusDoesNotAllowSensitiveEdit(AdStatus status) {
        assertThat(status.canSubmitSensitiveEdit()).isFalse();
    }

    @Test
    @DisplayName("It should keep ACTIVE able to reach PENDING_MODERATION in the transition graph for sensitive edits.")
    void mayBecome_AllowActiveToPendingModeration_When_SensitiveEditPathNeeded() {
        assertThat(AdStatus.ACTIVE.mayBecome(AdStatus.PENDING_MODERATION)).isTrue();
        assertThat(AdStatus.ACTIVE.canSubmitForModeration()).isFalse();
        assertThat(AdStatus.ACTIVE.canSubmitSensitiveEdit()).isTrue();
    }
}
