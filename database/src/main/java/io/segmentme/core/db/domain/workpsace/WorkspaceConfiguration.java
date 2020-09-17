package io.segmentme.core.db.domain.workpsace;

import lombok.Data;

import java.util.List;

@Data
public class WorkspaceConfiguration {

    private List<String> knownDateFormats;
}
