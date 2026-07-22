package com.nextbuy.adhub.location.infrastructure.location.importdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LocationSampleNode(
        @JsonProperty("id") String sourceId,
        String name,
        String type,
        List<LocationSampleNode> children,
        @JsonProperty("select_all_children_text") String selectAllChildrenText
) {

    public List<LocationSampleNode> childrenOrEmpty() {
        return children == null ? List.of() : children;
    }
}
