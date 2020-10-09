package io.segmentme.core.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkspaceDatesValidationRequest {
    private List<String> formats;
    private List<String> datesToValidate;
}
