package io.segmentme.channelservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "io.segmentme")
@EnableAsync
public class BrokerService {

    public static void main(String[] args) {
        SpringApplication.run(BrokerService.class, args);
    }

    @Configuration
    public static class BrokerLeasingConfiguration {

//        static final ThreadLocal<LeaseReceiver> LEASE_RECEIVER = new ThreadLocal<>();
//
//        @Bean
//        public RSocketServerCustomizer rSocketBrokerServerCustomizer() {
//            return rSocketServer ->
//                rSocketServer
//                    .interceptors(ir -> ir.forRequester((RSocketInterceptor) r -> new LeaseWaitingRSocket(r, LEASE_RECEIVER.get())));
//                    .lease(() -> {
//                        UUID uuid = UUID.randomUUID();
//                        DefaultLeaseReceiver leaseReceiver = new DefaultLeaseReceiver(uuid);
//                        VegaLimitLeaseSender leaseSender = new VegaLimitLeaseSender(1000,
//                            // default vegaLimit concurrency
//                            1000,
//                            Schedulers.newSingle("lease-sender")
//                                .createWorker());
//
//                        LEASE_RECEIVER.set(leaseReceiver);
//                        return Leases.create()
//                            .sender(leaseSender)
//                            .receiver(leaseReceiver)
//                            .stats(new VegaLimitLeaseStats(uuid, leaseSender, VegasLimit.newDefault()));
//                    });
//        }
    }
}
