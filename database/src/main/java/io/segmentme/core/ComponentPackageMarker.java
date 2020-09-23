package io.segmentme.core;

import io.segmentme.core.db.config.DbConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DbConfiguration.class)
public class ComponentPackageMarker {
}
