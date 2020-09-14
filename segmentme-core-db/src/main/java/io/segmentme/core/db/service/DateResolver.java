package io.segmentme.core.db.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@UtilityClass
public class DateResolver {

    Optional<Instant> resolve(String candidate, List<DateTimeFormatter> formats) {
        return formats.stream().map(it -> {
            try {
                TemporalAccessor parse = it.parse(candidate);
                return parse;
            } catch (Throwable ex) {
                log.debug("Unable to parse {} to format {}", candidate, it);
                return null;
            }
        })
                .filter(Objects::nonNull)
                .findAny()
                .map(it -> {
                    if (!it.isSupported(ChronoField.SECOND_OF_DAY)) {
                        return Instant.from(ZonedDateTime.of(LocalDate.from(it), LocalTime.MIDNIGHT, ZoneId.systemDefault()));
                    }
                    return Instant.from(it);
                });
    }
}
