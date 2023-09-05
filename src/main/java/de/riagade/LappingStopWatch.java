package de.riagade;

import java.time.*;
import java.util.*;

public class LappingStopWatch {
	private final Map<String, Duration[]> laps;
	private final Instant start;
	private Instant end = Instant.MIN;

	private LappingStopWatch() {
		this.laps = new HashMap<>();
		this.start = Instant.now();
	}

	public static LappingStopWatch start() {
		return new LappingStopWatch();
	}

	/**
	 * adds an end time to this {@link LappingStopWatch} which will necessary for the usage of {@link #getFinal()}
	 */
	public void stop() {
		this.end = Instant.now();
	}

	/**
	 * adds a lap time in form of a {@link Duration} between the start time of this {@link LappingStopWatch} and the current time with the given name.<br>
	 * multiple laps with the same name are added with an increasing index and in order of creation.
	 *
	 * @param name the name of the lap
	 */
	public void lap(String name) {
		var duration = new Duration[]{Duration.between(start, Instant.now())};
		if(laps.containsKey(name)) {
			var oldLaps = laps.get(name);
			var newLaps = new Duration[oldLaps.length+1];
			System.arraycopy(oldLaps, 0, newLaps, 0, oldLaps.length);
			newLaps[newLaps.length-1] = duration[0];
			duration = newLaps;
		}
		laps.put(name, duration);
	}

	/**
	 * calculates the {@link Duration} it took between the start and end time of the {@link LappingStopWatch}
	 *
	 * @return the {@link Duration} between the start of the {@link LappingStopWatch} and it's end
	 * @throws UnsupportedOperationException if the {@link LappingStopWatch} was not stopped properly
	 */
	public Duration getFinal() {
		return getFinal(false);
	}

	/**
	 * calculates the {@link Duration} it took between the start and end time of the {@link LappingStopWatch}
	 *
	 * @param doStop weather or not to stop the {@link LappingStopWatch} when executing; if not already done
	 * @return the {@link Duration} between the start of the {@link LappingStopWatch} and it's end
	 * @throws UnsupportedOperationException if the {@link LappingStopWatch} was not stopped properly
	 */
	public Duration getFinal(boolean doStop) {
		if(end.isBefore(start)) {
			if(doStop) {
				stop();
			} else {
				throw new UnsupportedOperationException("the StopWatch has not been stopped properly");
			}
		}
		return Duration.between(start, end);
	}

	/**
	 * @return the {@link Duration} between the start of the {@link LappingStopWatch} and now
	 */
	public Duration getCurrent() {
		return Duration.between(start, Instant.now());
	}

	/**
	 * get the time as a {@link Duration} that a lap has taken, from the start of the StopWatch.
	 *
	 * @param name the name of the lap to get the duration from
	 * @return the first {@link Duration} found of the given lap name
	 * @throws IllegalArgumentException if no such lap was found
	 * @throws IndexOutOfBoundsException if the index of the lap does not exist
	 * @see #getLap(String, int)
	 */
	public Duration getLap(String name) {
		return getLap(name, 0);
	}

	/**
	 * get the time as a {@link Duration} that a lap has taken, from the start of the StopWatch.
	 *
	 * @param name the name of the lap to get the duration from
	 * @param index the index of the lap, given there could be more than one duration with the laps name
	 * @return the {@link Duration} of the given lap
	 * @throws IllegalArgumentException if no such lap was found
	 * @throws IndexOutOfBoundsException if the index of the lap does not exist
	 */
	public Duration getLap(String name, int index) {
		if(!laps.containsKey(name))
			throw new IllegalArgumentException(String.format("No Lap with name %s exists", name));
		var lap = laps.get(name);
		if(lap.length <= index)
			throw new IndexOutOfBoundsException(String.format("There is not lap with name %s and index %d", name, index));
		return lap[index];
	}

	/**
	 * get the time as a {@link Duration} between 2 laps.<br>
	 * the order does not matter.
	 *
	 * @param lap1 the first lap
	 * @param lap2 the second lap
	 * @return the absolute difference between the {@link Duration} of lap1 and lap2
	 * @see #getLap(String)
	 */
	public Duration getDifference(String lap1, String lap2) {
		return getDifference(lap1, 0, lap2, 0);
	}

	/**
	 * get the time as a {@link Duration} between 2 laps.<br>
	 * the order does not matter.
	 *
	 * @param lap1 the first lap
	 * @param index1 the index of the first lap
	 * @param lap2 the second lap
	 * @param index2 the index of the second lap
	 * @return the absolute difference between the {@link Duration} of lap1 and lap2
	 * @see #getLap(String, int)
	 */
	public Duration getDifference(String lap1, int index1, String lap2, int index2) {
		var time1 = getLap(lap1, index1);
		var time2 = getLap(lap2, index2);
		return time1.minus(time2).abs();
	}
}
