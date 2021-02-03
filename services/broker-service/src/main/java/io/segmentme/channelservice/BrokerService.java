package io.segmentme.channelservice;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.SocketAcceptor;
import io.rsocket.examples.transport.tcp.lease.advanced.common.*;
import io.rsocket.examples.transport.tcp.lease.advanced.controller.TasksHandlingRSocket;
import io.rsocket.lease.Leases;
import io.rsocket.plugins.RSocketInterceptor;
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
import java.util.concurrent.*;

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
        public RSocketServerCustomizer rSocketBrokerServerCustomizer() {
            BlockingQueue<Runnable> tasksQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

            ThreadPoolExecutor threadPoolExecutor =
                    new ThreadPoolExecutor(1, CONCURRENT_WORKERS_COUNT, 1, TimeUnit.MINUTES, tasksQueue);

            Scheduler workScheduler = Schedulers.fromExecutorService(threadPoolExecutor);

            LeaseManager leaseManager = new LeaseManager(CONCURRENT_WORKERS_COUNT, TASK_PROCESSING_TIME);

            Disposable.Composite disposable = Disposables.composite();


            return rSocketServer ->
                    rSocketServer.acceptor(SocketAcceptor.with(new TasksHandlingRSocket(disposable, workScheduler, TASK_PROCESSING_TIME)))
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
