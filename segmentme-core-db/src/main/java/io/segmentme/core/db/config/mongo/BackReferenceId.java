package io.segmentme.core.db.config.mongo;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface BackReferenceId {

    String value();
}
