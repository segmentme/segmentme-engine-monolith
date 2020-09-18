package io.segmentme.core.service.converter;

import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.service.dto.UserHolder;

public class UserHolderConverter {

    public static User toUser(UserHolder holder) {
        return (User) new User()
                .setEmail(holder.getEmail())
                .setName(holder.getName())
                .setPassword(holder.getPassword())
                .setId(holder.getId());
    }

    public static UserHolder toHolder(User user) {
        return new UserHolder()
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setPassword(user.getPassword())
                .setId(user.getId());
    }
}
