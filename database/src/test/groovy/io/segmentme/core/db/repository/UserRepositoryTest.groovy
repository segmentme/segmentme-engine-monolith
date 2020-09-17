package io.segmentme.core.db.repository

import io.segmentme.core.db.domain.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import static io.segmentme.core.db.helper.UserHelper.createUser

@SpringBootTest
class UserRepositoryTest extends Specification {


    @Autowired
    private UserRepository repository

    def 'save user'() {
        given:
        User user = createUser()
        when:
        def save = repository.save(user)

        then:
        def found = repository.findById(save.getId())
        found.get() == save
    }

}
