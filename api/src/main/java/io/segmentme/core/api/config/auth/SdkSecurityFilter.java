package io.segmentme.core.api.config.auth;

import io.segmentme.core.db.service.workspace.WorkspaceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class SdkSecurityFilter extends GenericFilterBean {

    private static final String INTEGRATION_POINT_KEY = "integration-point-key";

    private final RequestMatcher requestMatcher;

    private final WorkspaceService workspaceService;

    public SdkSecurityFilter(RequestMatcher requestMatcher, WorkspaceService workspaceService) {
        this.requestMatcher = requestMatcher;
        this.workspaceService = workspaceService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (requestMatcher.matches(httpRequest)) {
            String integrationPointKey = httpRequest.getHeader(INTEGRATION_POINT_KEY);
            log.info("Request with integration-point-key in header {}", integrationPointKey);

            if (StringUtils.isBlank(integrationPointKey)) {
                throw new AccessDeniedException("Integration Point Key not presented");
            }
            if (workspaceService.findByIntegrationPointKey(integrationPointKey).isEmpty()) {
                throw new AccessDeniedException("Invalid credentials");
            }
        }

        chain.doFilter(httpRequest, response);
    }
}
