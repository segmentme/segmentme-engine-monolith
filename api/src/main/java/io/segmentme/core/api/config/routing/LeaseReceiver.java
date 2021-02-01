package io.segmentme.core.api.config.routing;

import io.rsocket.Payload;
import io.rsocket.lease.Lease;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

public interface LeaseReceiver extends Consumer<Flux<Lease>>, Disposable {
	/**
	 * Subscribes to the given source if lease has been acquired successfully.
	 *
	 * If there are no available leases this method allows to listen to new incoming
	 * leases and delay some action (e.g . retry) until new valid lease has come in.
	 */
	<T> Mono<T> acquireLease(Mono<T> source);

	/**
	 * Subscribes to the given source if lease has been acquired successfully.
	 *
	 * If there are no available leases this method allows to listen to new incoming
	 * leases and delay some action (e.g . retry) until new valid lease has come in.
	 */
	Flux<Payload> acquireLease(Flux<Payload> source);
}
