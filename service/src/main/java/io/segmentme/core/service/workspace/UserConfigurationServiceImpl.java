package io.segmentme.core.service.workspace;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;

@Service
public class UserConfigurationServiceImpl implements UserConfigurationService {

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM),
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT),
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG),
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL),
            DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'").withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'").withZone(ZoneId.systemDefault()),
            DateTimeFormatter.ofPattern(DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern()),
            DateTimeFormatter.ofPattern(DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern())
    );

    @Override
    public List<DateTimeFormatter> getDateFormats() {
        return DATE_TIME_FORMATTERS;
    }
}
