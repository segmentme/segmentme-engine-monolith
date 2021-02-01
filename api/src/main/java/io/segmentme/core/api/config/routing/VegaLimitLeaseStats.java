package io.segmentme.core.api.config.routing;

import com.netflix.concurrency.limits.limit.VegasLimit;
import io.rsocket.lease.Lease;
import io.rsocket.lease.LeaseStats;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.LongSupplier;

public class VegaLimitLeaseStats implements LeaseStats {

	final UUID                 uuid;
	final VegaLimitLeaseSender parent;
	final VegasLimit           limitAlgorithm;

	final ConcurrentMap<Integer, Integer> inFlightMap = new ConcurrentHashMap<>();
	final ConcurrentMap<Integer, Long> timeMap = new ConcurrentHashMap<>();

	final LongSupplier clock = System::nanoTime;

	volatile Lease currentLease;

	public VegaLimitLeaseStats(UUID uuid, VegaLimitLeaseSender parent, VegasLimit limit) {
		this.uuid = uuid;
		this.parent = parent;
		this.limitAlgorithm = limit;
	}



	public void onNewLease(Lease lease) {
		currentLease = lease;
	}

    @Override
    public void onEvent(EventType eventType) {

    }
}
