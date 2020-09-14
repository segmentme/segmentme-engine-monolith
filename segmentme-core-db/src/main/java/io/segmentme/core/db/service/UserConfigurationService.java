package io.segmentme.core.db.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

public interface UserConfigurationService {

    List<DateTimeFormatter> getDateFormats();
}
