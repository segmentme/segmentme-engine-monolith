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
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class LeaseConfiguration {

    public static final int TASK_PROCESSING_TIME = 500;
    public static final int CONCURRENT_WORKERS_COUNT = 1;
    public static final int QUEUE_CAPACITY = 50;

    @Bean
    @ConditionalOnMissingBean
    public RSocketConnectorConfigurer rSocketConnectorConfigurer(RSocketMessageHandler messageHandler) {

        BlockingQueue<Runnable> tasksQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(1, CONCURRENT_WORKERS_COUNT, 1, TimeUnit.MINUTES, tasksQueue);

        Scheduler workScheduler = Schedulers.fromExecutorService(threadPoolExecutor);

        LeaseManager leaseManager = new LeaseManager(CONCURRENT_WORKERS_COUNT, TASK_PROCESSING_TIME);

        Disposable.Composite disposable = Disposables.composite();


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
}
