package io.segmentme.core.service.analysis.event.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.segmentme.core.db.domain.statistic.AnalyzedData;
import io.segmentme.core.db.service.statistic.AnalyzedDataService;
import io.segmentme.core.service.analysis.segment.AnalysisService;
import io.segmentme.core.service.dto.analysis.AnalysisData;
import io.segmentme.core.service.dto.analysis.SegmentAnalysisResult;
import io.segmentme.redis.config.MessagePublisher;
import io.segmentme.redis.domain.ConnectedClient;
import io.segmentme.redis.dto.ClientAnalysesStateChanged;
import io.segmentme.redis.dto.ReanalysisMessage;
import io.segmentme.redis.repository.ConnectedClientRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Data
public class AnalysisEventProcessor {
    private final AnalysisService analysisService;

    private final AnalyzedDataService analyzedDataService;

    private final ObjectMapper objectMapper;

    private final MessagePublisher clientAnalysisStateChangedPublisher;

    private final ConnectedClientRepository clientRepository;

    @EventListener(ReanalysisMessage.class)
    public void handleMessage(ReanalysisMessage redisMessage) {
        log.info("got it! {}", redisMessage);

        List<String> connectedClients = getConnectedClientIds(redisMessage.getIntegrationPointKey());
        List<AnalyzedData> lastAnalyzedData = getLastAnalyzedData(redisMessage.getIntegrationPointKey(), redisMessage.getSegmentId(), connectedClients);
        lastAnalyzedData.forEach(it -> this.reanalyze(redisMessage.getContextId(), it));
    }

    private void reanalyze(String contextId, AnalyzedData analyzedData) {
        try {
            AnalysisData analysisData = new AnalysisData()
                    .setClientId(analyzedData.getClientId())
                    .setPayload(objectMapper.readValue(analyzedData.getPayload(), JsonNode.class));

            List<SegmentAnalysisResult> analysisResults = this.analysisService.analyze(analyzedData.getIntegrationPointKey(), contextId, analysisData);

            ClientAnalysesStateChanged message = new ClientAnalysesStateChanged()
                    .setClientId(analyzedData.getClientId())
                    .setIntegrationPointKey(analyzedData.getIntegrationPointKey())
                    .setBody(new ClientAnalysesStateChanged.ChangedAnalysis()
                            .setAnalyzedSegments(objectMapper.readValue(objectMapper.writeValueAsString(analysisResults), new TypeReference<>() {
                            })));

            clientAnalysisStateChangedPublisher.publish(message);
        } catch (JsonProcessingException e) {
            log.warn("Unable to parse json");
        }
    }

    private List<AnalyzedData> getLastAnalyzedData(String integrationPointKey, String segmentId, List<String> connectedClients) {
        List<AnalyzedData> latestClientsAnalyzedData = analyzedDataService.findLatestClientsAnalyzedData(integrationPointKey, segmentId, connectedClients);
        return latestClientsAnalyzedData;
    }


    private List<String> getConnectedClientIds(String integrationPointKey) {
        return clientRepository.findByIntegrationPointKey(integrationPointKey)
                .stream()
                .map(ConnectedClient::getClientId)
                .distinct()
                .collect(Collectors.toList());
    }
}
