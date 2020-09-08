package io.segmentme.core.db.common


import io.segmentme.core.db.models.TestModel
import io.segmentme.core.db.repository.TestModelRepository
import org.springframework.beans.factory.annotation.Autowired

class Test extends BaseDataJpa {

    @Autowired
    private TestModelRepository testModelRepository

    def 'testMongoConnection'() {
        given:
        def modelToSave = new TestModel().setName("Model")
        when:
        def savedModel = testModelRepository.save(modelToSave)
        then:
        def existedModel = testModelRepository.findById(savedModel.getId())
        existedModel != null
    }
}
