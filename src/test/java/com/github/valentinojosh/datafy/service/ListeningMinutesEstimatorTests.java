package com.github.valentinojosh.datafy.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListeningMinutesEstimatorTests {
    private final ListeningMinutesEstimator estimator = new ListeningMinutesEstimator();

    @Test
    void advancesOffsetAndStopsAfterPartialPage() throws Exception {
        Deque<int[]> pages = new ArrayDeque<>();
        pages.add(filledPage(50, 60_000));
        pages.add(filledPage(2, 60_000));
        List<Integer> offsets = new ArrayList<>();

        float result = estimator.estimateAnnualMinutes((offset, limit) -> {
            offsets.add(offset);
            assertEquals(50, limit);
            return pages.remove();
        });

        assertEquals(List.of(0, 50), offsets);
        assertEquals(52 * 52, result);
    }

    @Test
    void stopsAtMaximumTrackBoundaryWithoutExtraRequest() throws Exception {
        List<Integer> offsets = new ArrayList<>();

        float result = estimator.estimateAnnualMinutes((offset, limit) -> {
            offsets.add(offset);
            return filledPage(limit, 60_000);
        });

        assertEquals(List.of(0, 50, 100, 150, 200, 250, 300), offsets);
        assertEquals(350 * 52, result);
    }

    @Test
    void handlesEmptyAndInvalidDurations() throws Exception {
        assertEquals(0, estimator.estimateAnnualMinutes((offset, limit) -> new int[0]));
        assertEquals(52, estimator.estimateAnnualMinutes((offset, limit) -> new int[]{60_000, -1}));
    }

    @Test
    void propagatesExternalApiFailure() {
        assertThrows(IllegalStateException.class,
                () -> estimator.estimateAnnualMinutes((offset, limit) -> {
                    throw new IllegalStateException("Spotify unavailable");
                }));
    }

    private int[] filledPage(int size, int durationMs) {
        int[] page = new int[size];
        Arrays.fill(page, durationMs);
        return page;
    }
}
