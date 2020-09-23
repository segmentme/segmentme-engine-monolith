package io.segmentme.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EnableMongoRepositories(basePackageClasses = ComponentPackageMarker.class)
public final class ComponentPackageMarker {
}
