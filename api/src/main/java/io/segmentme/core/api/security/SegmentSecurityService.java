package io.segmentme.core.api.security;

import io.segmentme.core.service.analysis.segment.SegmentManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentSecurityService {

    private final SegmentManager segmentManager;

    private final SecurityService securityService;

    public boolean isManaged(String segmentId, String userId) {
        return securityService.isValidIntegrationPointKey(segmentManager.findById(segmentId)
                .getIntegrationPointKey(), userId);
    }
}
