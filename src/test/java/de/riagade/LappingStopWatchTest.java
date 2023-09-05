package de.riagade;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LappingStopWatchTest {

	public static final int WAITING_TIME_MS = 1;

	@Test
	void finalTime_availableAfterStop() throws InterruptedException {
		// Given
		var watch = LappingStopWatch.start();

		// When
		Thread.sleep(WAITING_TIME_MS);
		watch.stop();

		// Then
		var finalTime = watch.getFinal();
		printNanos("finalTime", finalTime);
		assertTrue(finalTime.compareTo(Duration.ofMillis(WAITING_TIME_MS)) >= 0,
				"finalTime should not be smaller than waitingTime");
	}

	@Test
	void finalTime_errorBeforeStop() {
		// Given
		var watch = LappingStopWatch.start();

		// When
		assertThrows(UnsupportedOperationException.class, watch::getFinal);
	}

	@Test
	void finalTime_noErrorWithParameterStop() throws InterruptedException {
		// Given
		var watch = LappingStopWatch.start();

		// When
		Thread.sleep(WAITING_TIME_MS);
		var finalTime = watch.getFinal(true);
		printNanos("finalTime", finalTime);

		// Then
		assertTrue(finalTime.compareTo(Duration.ofMillis(WAITING_TIME_MS)) >= 0,
				"finalTime should not be smaller than waitingTime");
	}

	@Test
	void current_timeIsRisingWithEveryRequest() throws InterruptedException {
		// Given
		var watch = LappingStopWatch.start();

		// When
		var current1 = watch.getCurrent();
		Thread.sleep(WAITING_TIME_MS);
		var current2 = watch.getCurrent();

		// Then
		printNanos("current1", current1);
		printNanos("current2", current2);
		assertFalse(current1.compareTo(current2) >= 0,
				"current1 should be smaller than current 2");
	}

	@Test
	void lap_savedToName() throws InterruptedException {
		// Given
		var lapName = randomString();
		var watch = LappingStopWatch.start();

		// When
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName);

		// Then
		var lapTime = watch.getLap(lapName);
		printNanos("lapTime", lapTime);
		assertTrue(lapTime.compareTo(Duration.ofMillis(WAITING_TIME_MS)) >= 0,
				"lapTime should not be smaller than waitingTime");
	}

	@Test
	void lap_errorOnUnknownLap() {
		// Given
		var unknownLap = randomString();
		var watch = LappingStopWatch.start();

		// When
		assertThrows(IllegalArgumentException.class, () -> watch.getLap(unknownLap));
	}

	@Test
	void lap_sameNamesAreSavedToIndexes() throws InterruptedException {
		// Given
		var lapName = randomString();
		var watch = LappingStopWatch.start();

		// When
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName);
		watch.lap(lapName);

		// Then
		var lapTime1 = watch.getLap(lapName, 0);
		var lapTime2 = watch.getLap(lapName, 1);
		printNanos("lapTime1", lapTime1);
		printNanos("lapTime2", lapTime2);
		assertTrue(lapTime1.compareTo(Duration.ofMillis(WAITING_TIME_MS)) >= 0,
				"lapTime1 should not be smaller than waitingTime");
		assertTrue(lapTime2.compareTo(Duration.ofMillis(WAITING_TIME_MS)) >= 0,
				"lapTime2 should not be smaller than waitingTime");
	}

	@Test
	void lap_errorOnUnknownIndex() throws InterruptedException {
		// Given
		var lapName = randomString();
		var watch = LappingStopWatch.start();
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName);

		// When
		assertThrows(IndexOutOfBoundsException.class, () -> watch.getLap(lapName, 1));
	}

	@Test
	void lap_sameNamesAreOrdered() throws InterruptedException {
		// Given
		var lapName = randomString();
		var watch = LappingStopWatch.start();

		// When
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName);
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName);

		// Then
		var lapTime1 = watch.getLap(lapName, 0);
		var lapTime2 = watch.getLap(lapName, 1);
		printNanos("lapTime1", lapTime1);
		printNanos("lapTime2", lapTime2);
		assertTrue(lapTime1.compareTo(Duration.ofMillis(WAITING_TIME_MS)) >= 0,
				"lapTime1 should not be smaller than waitingTime");
		assertTrue(lapTime2.compareTo(lapTime1) >= 0,
				"lapTime2 should not be smaller than lapTime1");
	}

	@Test
	void difference_isShorterThanLapTime() throws InterruptedException {
		// Given
		var lapName1 = randomString();
		var lapName2 = randomString();
		var watch = LappingStopWatch.start();
		Thread.sleep(WAITING_TIME_MS*2);
		watch.lap(lapName1);
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName2);

		// When
		var difference = watch.getDifference(lapName1, lapName2);

		// Then
		var lapTime1 = watch.getLap(lapName1);
		var lapTime2 = watch.getLap(lapName2);
		printNanos("difference", difference);
		printNanos("lapTime1", lapTime1);
		printNanos("lapTime2", lapTime2);
		assertTrue(lapTime1.compareTo(difference) >= 0,
				"lapTime1 should not be smaller than duration between lapTime1 and lapTime2");
		assertTrue(lapTime2.compareTo(difference) >= 0,
				"lapTime2 should not be smaller than duration between lapTime1 and lapTime2");
	}

	@Test
	void difference_orderIsIrrelevant() throws InterruptedException {
		// Given
		var lapName1 = randomString();
		var lapName2 = randomString();
		var watch = LappingStopWatch.start();
		Thread.sleep(WAITING_TIME_MS*2);
		watch.lap(lapName1);
		Thread.sleep(WAITING_TIME_MS);
		watch.lap(lapName2);

		// When
		var differenceOrdered = watch.getDifference(lapName1, lapName2);
		var differenceUnordered = watch.getDifference(lapName2, lapName1);

		// Then
		printNanos("differenceOrdered", differenceOrdered);
		printNanos("differenceUnordered", differenceUnordered);
		assertEquals(differenceOrdered, differenceUnordered);
	}

	private String randomString() {
		return UUID.randomUUID().toString();
	}

	private void printNanos(String info, Duration finalTime) {
		var nanos = finalTime.toNanos();
		System.out.printf("%s: %sms%n", info, String.format("%,f", nanos/1_000_000f));
	}
}