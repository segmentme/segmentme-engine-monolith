package io.segmentme.db.config.domain;

import lombok.Data;
import org.springframework.data.annotation.*;

import java.time.Instant;

@Data
public class DbObject {
    @Id
    private String id;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;
}
