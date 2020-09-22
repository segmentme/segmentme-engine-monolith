package io.segmentme.core.service.helper;

import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Arrays;

@UtilityClass
public class WorkspaceConfigurationHelper {

    public WorkspaceConfiguration defaultWorkspaceConfiguration() {
        return new WorkspaceConfiguration().setKnownDateFormats(Arrays.asList(
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.getPattern(),
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern(),
                DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern() + "'Z'",
                DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern(),
                DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.getPattern() + "'Z'",
                DateFormatUtils.ISO_8601_EXTENDED_TIME_TIME_ZONE_FORMAT.getPattern(),
                DateFormatUtils.SMTP_DATETIME_FORMAT.getPattern()));
    }
}
