package io.segmentme.channelservice;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
import io.segmentme.channelservice.lease.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "io.segmentme")
@EnableAsync
public class BrokerService {

    public static void main(String[] args) {
        SpringApplication.run(BrokerService.class, args);
    }

    @Configuration
    public static class BrokerLeasingConfiguration {

        static final ThreadLocal<LeaseReceiver> LEASE_RECEIVER = new ThreadLocal<>();

        @Bean
        public RSocketServerCustomizer rSocketBrokerServerCustomizer() {
            return rSocketServer ->
                rSocketServer
                    .interceptors(ir -> ir.forRequester((RSocketInterceptor) r -> new LeaseWaitingRSocket(r, LEASE_RECEIVER.get())))
                    .lease(() -> {
                        UUID uuid = UUID.randomUUID();
                        DefaultLeaseReceiver leaseReceiver = new DefaultLeaseReceiver(uuid);
                        VegaLimitLeaseSender leaseSender = new VegaLimitLeaseSender(1000,
                            // default vegaLimit concurrency
                            1000,
                            Schedulers.newSingle("lease-sender")
                                .createWorker());

                        LEASE_RECEIVER.set(leaseReceiver);
                        return Leases.create()
                            .sender(leaseSender)
                            .receiver(leaseReceiver)
                            .stats(new VegaLimitLeaseStats(uuid, leaseSender, VegasLimit.newDefault()));
                    });
        }
    }
}
