package io.segmentme.core.api.config.routing;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.examples.transport.tcp.lease.advanced.common.DefaultDeferringLeaseReceiver;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseManager;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseWaitingRSocket;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LimitBasedLeaseSender;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;

@Configuration
public class LeaseConfiguration {

    public static final int TASK_PROCESSING_TIME = 50;
    public static final int CONCURRENT_WORKERS_COUNT = 50;
    public static final int QUEUE_CAPACITY = 10000;

    @Bean
    @ConditionalOnMissingBean
    public RSocketConnectorConfigurer rSocketConnectorConfigurer(RSocketMessageHandler messageHandler) {


        LeaseManager leaseManager = new LeaseManager(CONCURRENT_WORKERS_COUNT, TASK_PROCESSING_TIME);


        return rSocketServer ->
                rSocketServer.acceptor(messageHandler.responder())
                        .lease(

                                (registry) -> {
                                    DefaultDeferringLeaseReceiver leaseReceiver =
                                            new DefaultDeferringLeaseReceiver(UUID.randomUUID().toString());

                                    registry.forRequester(
                                            (RSocketInterceptor) r -> new LeaseWaitingRSocket(r, leaseReceiver));

                                    final LimitBasedLeaseSender leaseSender =
                                            new LimitBasedLeaseSender(
                                                    UUID.randomUUID().toString(),
                                                    leaseManager,
                                                    VegasLimit.newBuilder()
                                                            .initialLimit(CONCURRENT_WORKERS_COUNT)
                                                            .maxConcurrency(QUEUE_CAPACITY)
                                                            .build());

                                    registry.forRequestsInResponder(__ -> leaseSender);

                                    return Leases.create().receiver(leaseReceiver).sender(leaseSender);
                                });
    }


    @Bean
    public ThreadPoolTaskExecutor channelExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("channel-executor-");
        return executor;
    }
}
