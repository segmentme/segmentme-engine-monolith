package io.segmentme.channelservice;

import com.netflix.concurrency.limits.limit.Gradient2Limit;
import io.rsocket.examples.transport.tcp.lease.advanced.common.DefaultDeferringLeaseReceiver;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseManager;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseWaitingRSocket;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LimitBasedLeaseSender;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class BrokerService {

    public static void main(String[] args) {
        SpringApplication.run(BrokerService.class, args);
    }


    @Configuration
    @RequiredArgsConstructor
    public static class BrokerLeasingConfiguration {

        private final SegmentMeRsocketConfiguration rsocketConfiguration;

        @Bean
        public RSocketServerCustomizer rSocketBrokerServerCustomizer() {
            log.info("Configure broker with settings: {}", rsocketConfiguration);
            var leaseSettings = rsocketConfiguration.getLease();
            LeaseManager leaseManager = new LeaseManager(leaseSettings.getCapacity(), leaseSettings.getTtl());
            return rSocketServer ->
                rSocketServer
                    .lease((registry) -> {
                        DefaultDeferringLeaseReceiver leaseReceiver =
                            new DefaultDeferringLeaseReceiver(UUID.randomUUID().toString());
                        registry.forRequester((RSocketInterceptor) r -> new LeaseWaitingRSocket(r, leaseReceiver));

                        final LimitBasedLeaseSender leaseSender =
                            new LimitBasedLeaseSender(
                                UUID.randomUUID().toString(),
                                leaseManager,
                                Gradient2Limit.newBuilder().initialLimit(leaseSettings.getVegasLimit().getInitialLimit()).maxConcurrency(leaseSettings.getVegasLimit().getMaxConcurrency()).build());

                        registry.forRequestsInResponder(__ -> leaseSender);

                        return Leases.create().receiver(leaseReceiver).sender(leaseSender);
                    });
        }
    }

}
