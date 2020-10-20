package io.segmentme.core.db.domain.workpsace;

import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class WorkspaceConfiguration {

    public static final List<String> DEFAULT_DATE_PATTERNS = Arrays.asList(
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'",
        DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
        DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'",
        DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
        DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern());



    private List<String> knownDateFormats;

    public List<DateTimeFormatter> toDateFormatters(List<String> formats) {
        return formats
                .stream()
                .map(DateTimeFormatter::ofPattern)
                .map(it -> it.withZone(ZoneId.systemDefault()))
                .collect(Collectors.toList());
    }

}
