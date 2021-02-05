package io.segmentme.channelservice.config;

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
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Configuration
public class LeaseConfiguration {

    public static final int TASK_PROCESSING_TIME = 50;
    public static final int CONCURRENT_WORKERS_COUNT = 5;
    public static final int QUEUE_CAPACITY = 50;

    @Bean
    @ConditionalOnMissingBean
    public RSocketConnectorConfigurer rSocketConnectorConfigurer(RSocketMessageHandler messageHandler) {
        LeaseManager leaseManager = new LeaseManager(CONCURRENT_WORKERS_COUNT, TASK_PROCESSING_TIME);
        return rSocketServer ->
            rSocketServer
                .reconnect(Retry.backoff(50, Duration.ofMillis(500)))
                .acceptor(messageHandler.responder())
                .lease((registry) -> {
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

                    return Leases.create().receiver(leaseReceiver);
                });
    }
}
