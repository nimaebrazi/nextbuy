package com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1;


import com.nextbuy.adhub.ad.application.command.create.AdCreateCommandHandler;
import com.nextbuy.adhub.ad.application.command.create.AdCreatedResult;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommandHandler;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationResult;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdRequest;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.SubmitAdForModerationResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.mapper.AdWebMapper;
import com.nextbuy.adhub.shared.advice.annotation.SuccessMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ads")
public class AdController {

    private final static String OWNER_ID_HEADER = "X-Owner-Id";

    private final AdWebMapper mapper;
    private final AdCreateCommandHandler createHandler;
    private final AdSubmitForModerationCommandHandler submitForModerationHandler;

    @PostMapping("")
    @SuccessMessage("ad.created.draft")
    public CreateAdResponse create(
            @RequestHeader(OWNER_ID_HEADER) long ownerId,
            @Valid @RequestBody CreateAdRequest request) {

        AdCreatedResult result = createHandler.handle(
                mapper.toCreateCommand(ownerId, request)
        );

        return mapper.toResponse(result);
    }

    @PostMapping("/{adId}/submit")
    @SuccessMessage("ad.submitted")
    public SubmitAdForModerationResponse submitForModeration(
            @PathVariable UUID adId,
            @RequestHeader(OWNER_ID_HEADER) long ownerId) {

        AdSubmitForModerationResult result = submitForModerationHandler.handle(
                mapper.toSubmitCommand(adId, ownerId)
        );

        return mapper.toSubmitResponse(result);
    }

}
