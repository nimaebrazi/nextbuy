package com.nextbuy.adhub.location.infrastructure.presentation.web.controller.v1;

import com.nextbuy.adhub.location.application.query.place.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
public class PlaceController {

    private final GetCityMapQueryHandler getCityMapQueryHandler;
    private final GetNeighbourhoodMapQueryHandler getNeighbourhoodMapQueryHandler;

    @GetMapping("/{citySlug}/map")
    ResponseEntity<GetCityMapResult> cityMap(
            @PathVariable String citySlug,
            @RequestParam(required = false) String categorySlug) {

        return ResponseEntity.ok(
                getCityMapQueryHandler.handle(new GetCityMapQuery(citySlug, categorySlug)));
    }

    @GetMapping("/{citySlug}/neighbourhoods/{neighbourhoodSlug}/map")
    ResponseEntity<GetNeighbourhoodMapResult> neighbourhoodMap(
            @PathVariable String citySlug,
            @PathVariable String neighbourhoodSlug) {

        return ResponseEntity.ok(
                getNeighbourhoodMapQueryHandler.handle(
                        new GetNeighbourhoodMapQuery(citySlug, neighbourhoodSlug)));
    }
}
