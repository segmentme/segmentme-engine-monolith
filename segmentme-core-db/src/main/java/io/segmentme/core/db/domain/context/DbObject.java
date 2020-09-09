package io.segmentme.core.db.domain.context;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class DbObject {
    @Id
    private String id;
}
