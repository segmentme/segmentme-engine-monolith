package io.segmentme.core.service.workspace;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface UserConfigurationService {

    List<DateTimeFormatter> getDateFormats();
}
