package io.segmentme.core.db.domain.user;

import io.segmentme.core.db.domain.context.DbObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "user")
public class User extends DbObject {
    @Indexed(unique = true)
    private String email;

    private String name;

    private String password;

}
