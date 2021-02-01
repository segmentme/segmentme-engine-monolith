package io.segmentme.core.api.config.routing;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Configuration
public class LeaseConfiguration {
    static final ThreadLocal<LeaseReceiver> LEASE_RECEIVER = new ThreadLocal<>();

    @Bean
    @ConditionalOnMissingBean
    public RSocketConnectorConfigurer rSocketConnectorConfigurer(RSocketMessageHandler messageHandler, VegaLimitLeaseSender leaseSender) {
        return connector -> connector //.addRequesterPlugin(interceptor)
            .interceptors(ir -> ir.forRequester((RSocketInterceptor) r -> new LeaseWaitingRSocket(r, LEASE_RECEIVER.get())))
            .lease(() -> {
                UUID uuid = UUID.randomUUID();
                DefaultLeaseReceiver leaseReceiver =
                    new DefaultLeaseReceiver(uuid);
                LEASE_RECEIVER.set(leaseReceiver);
                return Leases
                    .create()
                    .stats(new VegaLimitLeaseStats(
                        uuid,
                        leaseSender,
                        VegasLimit.newBuilder()
                            .maxConcurrency(4)
                            .initialLimit(1)
                            .build()
                    ))
                    .sender(leaseSender)
                    .receiver(leaseReceiver);
            })
            .acceptor(messageHandler.responder());
    }


    @Bean
    public VegaLimitLeaseSender vegaLeaseSender() {
        return new VegaLimitLeaseSender(
            4,
            1000,
            Schedulers.newSingle("lease-sender").createWorker()
        );
    }
}
