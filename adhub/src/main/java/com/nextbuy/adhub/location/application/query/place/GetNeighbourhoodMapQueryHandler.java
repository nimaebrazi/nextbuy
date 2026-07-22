package com.nextbuy.adhub.location.application.query.place;

import org.springframework.stereotype.Service;

@Service
public class GetNeighbourhoodMapQueryHandler {

    public GetNeighbourhoodMapResult handle(GetNeighbourhoodMapQuery query) {
        return new GetNeighbourhoodMapResult();
    }
}
