package io.segmentme.core.api.config.routing;

import com.netflix.concurrency.limits.limit.Gradient2Limit;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseManager;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LimitBasedLeaseSender;
import io.rsocket.lease.Leases;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LeaseConfiguration {

    private final SegmentMeRsocketConfiguration rsocketConfiguration;

    @Bean
    public RSocketConnectorConfigurer rSocketConnectorConfigurer(RSocketMessageHandler messageHandler) {
        log.info("Configure routing client with settings: {}", rsocketConfiguration);
        var leaseSettings = rsocketConfiguration.getLease();

        LeaseManager leaseManager = new LeaseManager(leaseSettings.getCapacity(), leaseSettings.getTtl());
        return rSocketServer ->
            rSocketServer.acceptor(messageHandler.responder())
                .lease((registry) -> {
                    final LimitBasedLeaseSender leaseSender =
                        new LimitBasedLeaseSender(
                            UUID.randomUUID().toString(),
                            leaseManager,
                            Gradient2Limit.newBuilder()
                                .initialLimit(leaseSettings.getVegasLimit().getInitialLimit())
                                .maxConcurrency(leaseSettings.getVegasLimit().getMaxConcurrency())
                                .build());
                    registry.forRequestsInResponder(__ -> leaseSender);
                    return Leases.create().sender(leaseSender);
                });
    }


    @Bean
    public ThreadPoolExecutor channelExecutor() {
        SegmentMeRsocketConfiguration.AnalysisThreadPoolSettings analysisThreadPool = rsocketConfiguration.getAnalysisThreadPool();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(analysisThreadPool.getCorePoolSize(), analysisThreadPool.getMaxPoolSize(),
            analysisThreadPool.getKeepAliveMs(), TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(300));
//        executor.setThreadNamePrefix("channel-executor-");
        return executor;
    }


    @Bean
    public Scheduler analyseScheduler(ThreadPoolExecutor channelExecutor) {
        return Schedulers.fromExecutor(channelExecutor);
    }
}
