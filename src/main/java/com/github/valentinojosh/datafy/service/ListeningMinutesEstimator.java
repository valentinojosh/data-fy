package com.github.valentinojosh.datafy.service;

import org.springframework.stereotype.Component;

@Component
public class ListeningMinutesEstimator {
    static final int PAGE_SIZE = 50;
    static final int MAX_TRACKS = 350;
    private static final int WEEKS_PER_YEAR = 52;
    private static final float MILLISECONDS_PER_MINUTE = 60_000f;

    @FunctionalInterface
    public interface DurationPageFetcher {
        int[] fetch(int offset, int limit) throws Exception;
    }

    public float estimateAnnualMinutes(DurationPageFetcher pageFetcher) throws Exception {
        long totalDurationMs = 0;
        int offset = 0;
        int tracksCounted = 0;

        while (tracksCounted < MAX_TRACKS) {
            int limit = Math.min(PAGE_SIZE, MAX_TRACKS - tracksCounted);
            int[] durations = pageFetcher.fetch(offset, limit);
            if (durations == null || durations.length == 0) {
                break;
            }

            int pageLength = Math.min(durations.length, limit);
            for (int index = 0; index < pageLength; index++) {
                totalDurationMs += Math.max(durations[index], 0);
            }

            tracksCounted += pageLength;
            offset += pageLength;
            if (pageLength < limit) {
                break;
            }
        }

        return (totalDurationMs / MILLISECONDS_PER_MINUTE) * WEEKS_PER_YEAR;
    }
}
