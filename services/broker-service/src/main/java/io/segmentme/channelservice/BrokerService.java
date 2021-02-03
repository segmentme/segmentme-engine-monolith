package io.segmentme.channelservice;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.examples.transport.tcp.lease.advanced.common.DefaultDeferringLeaseReceiver;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseManager;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseWaitingRSocket;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LimitBasedLeaseSender;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.UUID;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "io.segmentme")
@EnableAsync
public class BrokerService {

    public static void main(String[] args) {
        SpringApplication.run(BrokerService.class, args);
    }

    public static final int TASK_PROCESSING_TIME = 50;
    public static final int CONCURRENT_WORKERS_COUNT = 50;
    public static final int QUEUE_CAPACITY = 10000;

    @Configuration
    public static class BrokerLeasingConfiguration {

        @Bean
        public RSocketServerCustomizer rSocketBrokerServerCustomizer() {
            LeaseManager leaseManager = new LeaseManager(CONCURRENT_WORKERS_COUNT, TASK_PROCESSING_TIME);
            return rSocketServer ->
                    rSocketServer
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
    }

}
