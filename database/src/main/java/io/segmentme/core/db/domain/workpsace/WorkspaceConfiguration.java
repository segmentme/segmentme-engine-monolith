package io.segmentme.core.db.domain.workpsace;

import lombok.Data;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WorkspaceConfiguration {

    private List<String> knownDateFormats;

    public List<DateTimeFormatter> toDateFormatters(List<String> formats) {
        return formats
                .stream()
                .map(DateTimeFormatter::ofPattern)
                .map(it -> it.withZone(ZoneId.systemDefault()))
                .collect(Collectors.toList());
    }
}
