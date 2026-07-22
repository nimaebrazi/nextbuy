package com.nextbuy.adhub.location.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class CountryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "iso_code", nullable = false, length = 2)
    private String isoCode;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "centroid", columnDefinition = "geometry(Point,4326)")
    private Point centroid;

    @Column(name = "boundary", columnDefinition = "geometry(MultiPolygon,4326)")
    private MultiPolygon boundary;

    // DB trigger computes this from boundary — never written by JPA
    @Column(name = "bbox", columnDefinition = "geometry(Polygon,4326)", insertable = false, updatable = false)
    private Polygon bbox;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CountryEntity() {
    }
}
