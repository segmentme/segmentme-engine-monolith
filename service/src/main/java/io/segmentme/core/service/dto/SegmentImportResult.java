package io.segmentme.core.service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SegmentImportResult {
    private List<String> created;

    private List<String> updated;

    private Map<String, String> errors;
}
