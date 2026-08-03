package com.nextbuy.adhub.ad.slice;

import com.nextbuy.adhub.ad.application.command.approve.AdApproveCommandHandler;
import com.nextbuy.adhub.ad.application.command.approve.AdApproveResult;
import com.nextbuy.adhub.ad.application.command.create.AdCreateCommandHandler;
import com.nextbuy.adhub.ad.application.command.create.AdCreatedResult;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommandHandler;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationResult;
import com.nextbuy.adhub.ad.domain.exception.AdDomainException;
import com.nextbuy.adhub.ad.domain.model.AdStatus;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.AdController;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.AdApproveResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.SubmitAdForModerationResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.mapper.AdWebMapper;
import com.nextbuy.adhub.support.ad.controller.ControllerTestBase;
import com.nextbuy.adhub.support.ad.controller.JsonPaths;
import com.nextbuy.adhub.support.ad.fixtures.AdRequests;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@Tags({@Tag("slice"), @Tag("controller")})
@WebMvcTest(controllers = AdController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdControllerTest extends ControllerTestBase {

    private static final String ADS_PATH = "/api/v1/ads";

    @MockitoBean
    private AdWebMapper mapper;

    @MockitoBean
    private AdCreateCommandHandler adCreateCommandHandler;

    @MockitoBean
    private AdApproveCommandHandler adApproveCommandHandler;

    @MockitoBean
    private AdSubmitForModerationCommandHandler adSubmitForModerationCommandHandler;


    @Test
    void create_ItShouldCreateDraftAd() throws Exception {
        long ownerId = 1;
        var createdAt = Instant.now();
        var adId = UUID.randomUUID();

        var request = AdRequests.randomCreateAdRequest();
        var createCommand = mapper.toCreateCommand(ownerId, request);
        var result = new AdCreatedResult(adId, AdStatus.DRAFT.toString(), createdAt);
        var response = new CreateAdResponse(adId, AdStatus.DRAFT.toString(), createdAt);

        given(mapper.toCreateCommand(ownerId, request)).willReturn(createCommand);
        given(adCreateCommandHandler.handle(createCommand)).willReturn(result);
        given(mapper.toCreateResponse(result)).willReturn(response);


        var apiResult = mockMvcUtils.post(ADS_PATH)
                .userId(1)
                .body(request)
                .exchange();

        apiResult
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(JsonPaths.successEnvelope(ADS_PATH, "Ad draft created successfully."))
                .andExpect(JsonPaths.data("id", adId.toString()))
                .andExpect(JsonPaths.data("status", AdStatus.DRAFT.toString()))
                .andExpect(JsonPaths.data("createdAt", createdAt.toString()));
    }

    @Test
    void submit_ItShouldSubmitAdToPendingModerationStatus() throws Exception {
        long ownerId = 1;
        var submittedAt = Instant.now();
        var adId = UUID.randomUUID();

        var command = mapper.toSubmitCommand(adId, ownerId);
        var result = new AdSubmitForModerationResult(adId, AdStatus.PENDING_MODERATION.toString(), submittedAt);
        var response = new SubmitAdForModerationResponse(adId, AdStatus.PENDING_MODERATION.toString(), submittedAt);

        given(mapper.toSubmitCommand(adId, ownerId)).willReturn(command);
        given(adSubmitForModerationCommandHandler.handle(command)).willReturn(result);
        given(mapper.toSubmitResponse(result)).willReturn(response);


        String path = submitPath(adId);
        var apiResult = mockMvcUtils.post(path)
                .userId(1)
                .exchange();

        apiResult
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(JsonPaths.successEnvelope(path, "Ad submitted successfully."))
                .andExpect(JsonPaths.data("id", adId.toString()))
                .andExpect(JsonPaths.data("status", AdStatus.PENDING_MODERATION.toString()))
                .andExpect(JsonPaths.data("submittedAt", submittedAt.toString()));
    }

    @Test
    void submit_WhenInvalidStatus_ItShouldReturnLocalizedSafeMessage() throws Exception {
        UUID adId = UUID.randomUUID();
        long ownerId = 1L;
        var command = mapper.toSubmitCommand(adId, ownerId);

        given(mapper.toSubmitCommand(adId, ownerId)).willReturn(command);
        given(adSubmitForModerationCommandHandler.handle(any()))
                .willThrow(new AdDomainException.InvalidStatus(
                        AdStatus.ACTIVE.name(), AdStatus.PENDING_MODERATION.name()));

        String path = submitPath(adId);
        var apiResult = mockMvcUtils.post(path)
                .userId(ownerId)
                .exchange();

        apiResult
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(JsonPaths.domainRuleViolation(path,
                        "This ad cannot be processed in its current state."));
    }

    @Test
    void submit_WhenInvalidStatusAndFaLocale_ItShouldReturnPersianMessage() throws Exception {
        UUID adId = UUID.randomUUID();
        long ownerId = 1L;
        var command = mapper.toSubmitCommand(adId, ownerId);

        given(mapper.toSubmitCommand(adId, ownerId)).willReturn(command);
        given(adSubmitForModerationCommandHandler.handle(any()))
                .willThrow(new AdDomainException.InvalidStatus(
                        AdStatus.ACTIVE.name(), AdStatus.PENDING_MODERATION.name()));

        String path = submitPath(adId);
        var apiResult = mockMvcUtils.post(path)
                .userId(ownerId)
                .header("Accept-Language", "fa")
                .exchange();

        apiResult
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(JsonPaths.domainRuleViolation(path,
                        "آگهی در وضعیت فعلی قابل پردازش نیست."));
    }

    @Test
    void approve_ItShouldApproveDraftAd() throws Exception {
        UUID adId = UUID.randomUUID();
        Instant updatedAt = Instant.now();

        var approveCommand = mapper.toApproveCommand(adId);
        var result = new AdApproveResult(adId, AdStatus.ACTIVE.name(), updatedAt);
        var response = new AdApproveResponse(adId, AdStatus.ACTIVE.name(), updatedAt);

        given(mapper.toApproveCommand(adId)).willReturn(approveCommand);
        given(adApproveCommandHandler.handle(approveCommand)).willReturn(result);
        given(mapper.toApproveResponse(result)).willReturn(response);


        String path = approvePath(adId);
        var apiResult = mockMvcUtils.post(path).exchange();

        apiResult
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(JsonPaths.successEnvelope(path, "Ad approved successfully."))
                .andExpect(JsonPaths.data("id", adId.toString()))
                .andExpect(JsonPaths.data("status", AdStatus.ACTIVE.toString()))
                .andExpect(JsonPaths.data("updatedAt", updatedAt.toString()));
    }

    private static String submitPath(UUID adId) {
        return "/api/v1/ads/%s/submit".formatted(adId);
    }

    private static String approvePath(UUID adId) {
        return "/api/v1/ads/%s/approve".formatted(adId);
    }

}
