package io.segmentme.core.db.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "testModel")
public class TestModel {

    @Id
    private String id;

    private String name;
}

