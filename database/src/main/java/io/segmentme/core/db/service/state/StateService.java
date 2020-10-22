package io.segmentme.core.db.service.state;

import io.segmentme.core.db.domain.state.State;
import io.segmentme.core.db.repository.StateRepository;
import io.segmentme.core.db.service.AbstractDatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StateService extends AbstractDatabaseService<State, StateRepository> {

    public List<State> findByIntegrationPointKey(String integrationPointKey) {
        return repository.findByIntegrationPointKey(integrationPointKey);
    }
}
