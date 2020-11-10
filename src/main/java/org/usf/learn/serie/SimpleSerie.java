package org.usf.learn.serie;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static org.usf.learn.util.Asserts.requirePositif;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

@Getter
public class SimpleSerie<T extends Temporal & Comparable<? super T>, P> implements Serie<T, P> {

	private final T start;
	private final int step;
	private final ChronoUnit temporalUnit;
	private final List<P> points;
	
	public SimpleSerie(T start, int step, ChronoUnit temporalUnit, Collection<? extends P> c) {
		this.start = requireNonNull(start);
		this.step = requirePositif(step);
		this.temporalUnit = requireNonNull(temporalUnit);
		this.points = unmodifiableList(new ArrayList<>(requireNonNull(c)));
	}
	
}
