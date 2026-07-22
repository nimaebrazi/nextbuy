package com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.mapper;

import com.nextbuy.adhub.ad.application.command.create.AdCreateCommand;
import com.nextbuy.adhub.ad.application.command.create.AdCreatedResult;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationCommand;
import com.nextbuy.adhub.ad.application.command.submit.AdSubmitForModerationResult;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdRequest;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.CreateAdResponse;
import com.nextbuy.adhub.ad.infrastructure.presentation.web.controller.v1.dto.SubmitAdForModerationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AdWebMapper {

    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "price", source = "request.priceAmount")
    @Mapping(target = "currency", source = "request.priceCurrency")
    AdCreateCommand toCreateCommand(long ownerId, CreateAdRequest request);

    CreateAdResponse toResponse(AdCreatedResult result);

    AdSubmitForModerationCommand toSubmitCommand(UUID adId, long ownerId);

    @Mapping(target = "adId", source = "adId")
    SubmitAdForModerationResponse toSubmitResponse(AdSubmitForModerationResult result);
}
