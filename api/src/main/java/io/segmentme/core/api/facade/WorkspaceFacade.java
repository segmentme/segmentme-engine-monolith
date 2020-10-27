package io.segmentme.core.api.facade;

import io.segmentme.core.api.config.AuthUser;
import io.segmentme.core.api.dto.WorkspaceDatesValidationRequest;
import io.segmentme.core.api.dto.WorkspaceDatesValidationResponse;
import io.segmentme.core.api.dto.WorkspaceDetails;
import io.segmentme.core.api.dto.WorkspaceUserProfile;
import io.segmentme.core.db.domain.user.User;
import io.segmentme.core.db.domain.workpsace.IntegrationPoint;
import io.segmentme.core.db.domain.workpsace.UserProfile;
import io.segmentme.core.db.domain.workpsace.WorkspaceConfiguration;
import io.segmentme.core.service.dto.WorkspaceHolder;
import io.segmentme.core.service.user.UserManager;
import io.segmentme.core.service.utils.DateResolver;
import io.segmentme.core.service.workspace.UserProfileManager;
import io.segmentme.core.service.workspace.WorkspaceManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class WorkspaceFacade {

    private final WorkspaceManager workspaceManager;

    private final UserProfileManager userProfileManager;

    private final UserManager userManager;

    public WorkspaceDetails getWorkspaceDetails(String workspaceId) {
        return convertToWorkspaceDetails(workspaceManager.getWorkspace(workspaceId));
    }

    public WorkspaceDetails createWorkspace(String ownderId, String name) {
        return convertToWorkspaceDetails(workspaceManager.createWorkspace(ownderId, name));
    }

    private WorkspaceDetails convertToWorkspaceDetails(WorkspaceHolder holder) {
        return new WorkspaceDetails()
            .setId(holder.getId())
            .setName(holder.getName())
            .setIntegrationPoints(holder.getIntegrationPoints())
            .setConfiguration(holder.getWorkspaceConfiguration());
    }

    public IntegrationPoint addIntegrationPoint(AuthUser currentUser, String workspaceId, String name) {
        return workspaceManager.addIntegrationPoint(workspaceId, name);

    }

    public void updateConfiguration(String userId, String workspaceId, WorkspaceConfiguration workspaceConfiguration) {
        workspaceManager.updateConfiguration(workspaceId, new WorkspaceHolder().setWorkspaceConfiguration(workspaceConfiguration));
    }

    public void updateIntegrationPoint(String userId, String workspaceId, IntegrationPoint integrationPoint) {
        workspaceManager.updateIntegrationPoint(workspaceId, integrationPoint);
    }

    public void removeIntegrationPoint(String userId, String workspaceId, String key) {
        workspaceManager.removeIntegrationPoint(workspaceId, key);
    }

    public WorkspaceDatesValidationResponse validateDateFormats(String userId, String workspaceId,
                                                                WorkspaceDatesValidationRequest validationRequest) {

        Map<String, Optional<DateTimeFormatter>> patterns = validationRequest.getFormats().stream().collect(Collectors.toMap(it -> it, this::silentOfPattern));

        WorkspaceDatesValidationResponse workspaceDatesValidationResponse = new WorkspaceDatesValidationResponse();

        workspaceDatesValidationResponse.setFormats(patterns.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue().isPresent())));

        if (!CollectionUtils.isEmpty(validationRequest.getDatesToValidate())) {
            Map<String, DateTimeFormatter> validPatterns = patterns.entrySet().stream().filter(it -> it.getValue().isPresent()).collect(Collectors.toMap(Map.Entry::getKey, it -> it.getValue().get()));
            workspaceDatesValidationResponse.setDates(
                validationRequest.getDatesToValidate()
                    .stream().collect(HashMap::new, (m, v) -> m.put(v, tryToParseDate(v, validPatterns)), HashMap::putAll)
            );
        }

        return workspaceDatesValidationResponse;
    }

    private String tryToParseDate(String candidate, Map<String, DateTimeFormatter> validPatterns) {
        String format = validPatterns.entrySet().stream()
            .filter(it -> DateResolver.resolve(candidate, Arrays.asList(it.getValue())).isPresent())
            .map(Map.Entry::getKey).findAny().orElse(null);
        return format;
    }

    private Optional<DateTimeFormatter> silentOfPattern(String format) {
        try {
            DateTimeFormatter value = DateTimeFormatter.ofPattern(format);
            if (DateResolver.resolve(value.format(ZonedDateTime.now()), Arrays.asList(value)).isPresent()) {
                return Optional.of(value);
            }
            return Optional.empty();
        } catch (Throwable ex) {
            log.error("Unable to create date time formatter for pattern {}", format, ex);
        }
        return Optional.empty();
    }

    public void updateWorkspace(String userId, String workspaceId, WorkspaceDetails workspaceDetails) {
        workspaceManager.updateConfiguration(workspaceId, new WorkspaceHolder().setName(workspaceDetails.getName()).setWorkspaceConfiguration(workspaceDetails.getConfiguration()));
    }

    public void removeWorkspace(String id, String workspaceId) {
        workspaceManager.removeWorkspace(workspaceId);
    }

    public List<WorkspaceUserProfile> getWorkspaceProfiles(String workspaceId) {
        List<UserProfile> workspaceProfiles = userProfileManager.getWorkspaceProfiles(workspaceId);
        List<String> userIds = workspaceProfiles.stream().map(UserProfile::getUserId).collect(Collectors.toList());
        Map<String, User> users = userManager.getByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        return workspaceProfiles.stream().map(profile -> this.convertToWorkspaceProfile(profile, users.get(profile.getUserId()))).collect(Collectors.toList());
    }

    private WorkspaceUserProfile convertToWorkspaceProfile(UserProfile profile, User user) {
        return new WorkspaceUserProfile().setEmail(user.getEmail()).setName(user.getName()).setProfileId(profile.getId()).setUserId(user.getId()).setRole(profile.getRole());
    }
}
