package io.segmentme.channelservice;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.examples.transport.tcp.lease.advanced.common.DefaultDeferringLeaseReceiver;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseManager;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LeaseWaitingRSocket;
import io.rsocket.examples.transport.tcp.lease.advanced.common.LimitBasedLeaseSender;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
import io.rsocket.routing.broker.acceptor.BrokerSocketAcceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "io.segmentme")
@EnableAsync
public class BrokerService {

    public static void main(String[] args) {
        SpringApplication.run(BrokerService.class, args);
    }

    public static final int TASK_PROCESSING_TIME = 500;
    public static final int CONCURRENT_WORKERS_COUNT = 1;
    public static final int QUEUE_CAPACITY = 50;

    @Configuration
    public static class BrokerLeasingConfiguration {

        @Bean
        public RSocketServerCustomizer rSocketBrokerServerCustomizer(BrokerSocketAcceptor metadataExtractorBrokerSocketAcceptor) {
            BlockingQueue<Runnable> tasksQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

            ThreadPoolExecutor threadPoolExecutor =
                    new ThreadPoolExecutor(1, CONCURRENT_WORKERS_COUNT, 1, TimeUnit.MINUTES, tasksQueue);

            Scheduler workScheduler = Schedulers.fromExecutorService(threadPoolExecutor);

            LeaseManager leaseManager = new LeaseManager(CONCURRENT_WORKERS_COUNT, TASK_PROCESSING_TIME);

            Disposable.Composite disposable = Disposables.composite();


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
                                                        VegasLimit.newDefault());

                                        registry.forRequestsInResponder(__ -> leaseSender);

                                        return Leases.create().receiver(leaseReceiver).sender(leaseSender);
                                    });
        }
    }

}
