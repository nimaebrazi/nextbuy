package com.nextbuy.adhub.location.application.query.place;

import org.springframework.stereotype.Service;

@Service
public class GetCityMapQueryHandler {

    public GetCityMapResult handle(GetCityMapQuery query) {
        return new GetCityMapResult();
    }
}
