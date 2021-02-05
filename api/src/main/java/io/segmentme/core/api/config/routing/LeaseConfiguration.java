package io.segmentme.core.api.config.routing;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseManager;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LimitBasedLeaseSender;
import io.rsocket.lease.Leases;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Configuration
public class LeaseConfiguration {

    public static final int TIME_TO_LIVE = 100;
    public static final int SUPPOSED_TASK_PROCESSING = 50;

    @Bean
    @ConditionalOnMissingBean
    public RSocketConnectorConfigurer rSocketConnectorConfigurer(RSocketMessageHandler messageHandler, ThreadPoolTaskExecutor channelExecutor) {


        LeaseManager leaseManager = new LeaseManager(20, TIME_TO_LIVE);


        return rSocketServer ->
            rSocketServer.acceptor(messageHandler.responder())
                .lease((registry) -> {

                    final LimitBasedLeaseSender leaseSender =
                        new LimitBasedLeaseSender(
                            UUID.randomUUID().toString(),
                            leaseManager,
                            VegasLimit.newBuilder()
                                .initialLimit(15)
                                .maxConcurrency(10)
                                .build());

                    registry.forRequestsInResponder(__ -> leaseSender);

                    return Leases.create().sender(leaseSender);
                });
    }


    @Bean
    public ThreadPoolTaskExecutor channelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("channel-executor-");
        return executor;
    }

    @Bean
    public Scheduler analyseScheduler(ThreadPoolTaskExecutor channelExecutor){
        return Schedulers.fromExecutor(channelExecutor);
    }
}
