package io.segmentme.core.service.dto;

import lombok.Data;

import java.util.Map;

@Data
public class SegmentImportResult {
    private Integer created;

    private Integer updated;

    private Map<String, String> errors;
}
