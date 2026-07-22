package com.nextbuy.adhub.ad.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ad_locations")
@Getter
@Setter
public class AdLocationEntity {

    @Id
    @Column(name = "ad_id")
    private UUID adId;

    @Column(name = "country_id", nullable = false)
    private UUID countryId;

    @Column(name = "province_id", nullable = false)
    private UUID provinceId;

    @Column(name = "city_id", nullable = false)
    private UUID cityId;

    @Column(name = "neighbourhood_id")
    private UUID neighbourhoodId;

    @Column(name = "point", columnDefinition = "geometry(Point,4326)")
    private Point point;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public AdLocationEntity() {
    }
}
