package com.nextbuy.adhub.location.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class CityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "province_id", nullable = false)
    private UUID provinceId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "centroid", columnDefinition = "geometry(Point,4326)")
    private Point centroid;

    @Column(name = "boundary", columnDefinition = "geometry(MultiPolygon,4326)")
    private MultiPolygon boundary;

    // DB trigger computes these from boundary — never written by JPA
    @Column(name = "bbox", columnDefinition = "geometry(Polygon,4326)", insertable = false, updatable = false)
    private Polygon bbox;

    @Column(name = "bbox_min_lng", insertable = false, updatable = false)
    private Double bboxMinLng;

    @Column(name = "bbox_min_lat", insertable = false, updatable = false)
    private Double bboxMinLat;

    @Column(name = "bbox_max_lng", insertable = false, updatable = false)
    private Double bboxMaxLng;

    @Column(name = "bbox_max_lat", insertable = false, updatable = false)
    private Double bboxMaxLat;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public CityEntity() {
    }
}
