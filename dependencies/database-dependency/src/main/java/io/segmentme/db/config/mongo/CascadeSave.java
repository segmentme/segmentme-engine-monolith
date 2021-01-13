package io.segmentme.db.config.mongo;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CascadeSave {

}
