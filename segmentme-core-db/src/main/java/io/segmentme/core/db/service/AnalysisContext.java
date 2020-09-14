package io.segmentme.core.db.service;

import lombok.Data;

import java.util.Map;

@Data
public class AnalysisContext {
    Map<String, Object> values;
}
