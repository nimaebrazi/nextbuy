package com.nextbuy.adhub.ad.slice;

import com.nextbuy.adhub.ad.application.command.create.AdCreateCommandHandler;
import com.nextbuy.adhub.ad.application.command.create.AdCreatedResult;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommandHandler;
import com.nextbuy.adhub.ad.domain.model.AdStatus;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.AdController;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdResponse;
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

import static org.mockito.BDDMockito.given;


@Tags({@Tag("slice"), @Tag("controller")})
@WebMvcTest(controllers = AdController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdControllerTest extends ControllerTestBase {

    @MockitoBean
    private AdWebMapper mapper;

    @MockitoBean
    private AdCreateCommandHandler adCreateCommandHandler;

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
        given(mapper.toResponse(result)).willReturn(response);


        var apiResult = mockMvcUtils.post("/api/v1/ads")
                .ownerId(1)
                .body(request)
                .exchange();

        apiResult
                .andDo(MockMvcResultHandlers.print())
                .andExpect(JsonPaths.statusOk())
                .andExpectAll(JsonPaths.apiStructure())
                .andExpect(JsonPaths.isSuccess())
                .andExpect(JsonPaths.path("/api/v1/ads"))
                .andExpect(JsonPaths.adStatus(AdStatus.DRAFT.toString()))
                .andExpect(JsonPaths.createdAt(createdAt.toString()))
                .andExpect(JsonPaths.id(adId.toString()))
                .andExpect(JsonPaths.message("Ad draft, created successfully."));
    }

}
