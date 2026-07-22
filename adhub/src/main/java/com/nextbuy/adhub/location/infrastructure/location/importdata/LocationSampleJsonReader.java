package com.nextbuy.adhub.location.infrastructure.location.importdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class LocationSampleJsonReader {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public LocationSampleJsonReader(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    public LocationSampleFile read(String filePath) throws IOException {
        Resource resource = resolveResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return objectMapper.readValue(inputStream, LocationSampleFile.class);
        }
    }

    private Resource resolveResource(String filePath) {
        if (filePath.startsWith("classpath:") || filePath.startsWith("file:")) {
            return resourceLoader.getResource(filePath);
        }
        return resourceLoader.getResource("file:" + filePath);
    }
}
