package com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1;


import com.nextbuy.adhub.ad.application.command.approve.AdApproveCommandHandler;
import com.nextbuy.adhub.ad.application.command.approve.AdApproveResult;
import com.nextbuy.adhub.ad.application.command.create.AdCreateCommandHandler;
import com.nextbuy.adhub.ad.application.command.create.AdCreatedResult;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommandHandler;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationResult;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.AdApproveResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdRequest;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.SubmitAdForModerationResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.mapper.AdWebMapper;
import com.nextbuy.adhub.shared.advice.annotation.SuccessMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ads")
public class AdController {

    private final static String USER_ID_HEADER = "X-User-Id";

    private final AdWebMapper mapper;
    private final AdCreateCommandHandler createHandler;
    private final AdSubmitForModerationCommandHandler submitForModerationHandler;
    private final AdApproveCommandHandler approveHandler;

    @PostMapping("")
    @SuccessMessage("ad.created.draft")
    public CreateAdResponse create(
            @RequestHeader(USER_ID_HEADER) @Positive long ownerId,
            @Valid @RequestBody CreateAdRequest request, HttpServletRequest httpRequest) {

        httpRequest.getHeaderNames().asIterator().forEachRemaining(name ->
                log.debug("header {}={}", name, httpRequest.getHeader(name))
        );
        AdCreatedResult result = createHandler.handle(
                mapper.toCreateCommand(ownerId, request)
        );

        return mapper.toCreateResponse(result);
    }

    @PostMapping("/{adId}/submit")
    @SuccessMessage("ad.submitted")
    public SubmitAdForModerationResponse submitForModeration(
            @PathVariable UUID adId,
            @RequestHeader(USER_ID_HEADER) @Positive long ownerId) {

        AdSubmitForModerationResult result = submitForModerationHandler.handle(
                mapper.toSubmitCommand(adId, ownerId)
        );

        return mapper.toSubmitResponse(result);
    }

    @PostMapping("/{adId}/approve")
    @SuccessMessage("ad.approved")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') and hasAnyAuthority('AD_APPROVE')")
    public AdApproveResponse approve(@PathVariable UUID adId) {
        AdApproveResult result = approveHandler.handle(
                mapper.toApproveCommand(adId)
        );

        return mapper.toApproveResponse(result);
    }
}
