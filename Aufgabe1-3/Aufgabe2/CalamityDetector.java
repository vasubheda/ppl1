package Aufgabe2;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Part: Vishwas (simulation, immediate measures).
 *
 * Detects calamities from the daily weather WITHOUT knowing the calamity formulas of the Aufgabe1.Weather class:
 * a calamity changes several indicators significantly from one day to the next (a spike), slow drift and the
 * slow recovery to normal weather are no spikes (change per day below SPIKE_THRESHOLD of the indicator range).
 *
 * Signatures (from the "how the weather values are changed" part of the diagram):
 *   drought      : Temp up,   Water down, Humidity down   (Temp must spike + at least one more)
 *   flood        : Water up,  Humidity up                 (Water must spike + Humidity)
 *   flash freeze : Temp down, Humidity down               (Temp must spike + Humidity)
 *
 * It also keeps the calamity track record of the last years, which the long-term measures use to estimate
 * which calamity is probable in the future.
 *
 * Modularization unit: object (remembers yesterday's values and the track record of one run).
 */
public class CalamityDetector {

    public enum Type {DROUGHT, FLOOD, FLASH_FREEZE}

    /** a detected calamity with its severity (about 1.0 = nominal shock, between SEVERITY_MIN and SEVERITY_MAX) */
    public static final class Event {
        private final Type type;
        private final double severity;
        private final double score;

        private Event(Type type, double severity, double score) {
            this.type = type;
            this.severity = severity;
            this.score = score;
        }

        public Type getType() {
            return type;
        }

        public double getSeverity() {
            return severity;
        }
    }

    private double[] previous;
    private int suppressDaysLeft;
    private final int[] yearCounts = new int[Type.values().length];
    private final Deque<int[]> pastYears = new ArrayDeque<>();

    /**
     * Compares today's 7 indicator values with yesterday's.
     * Returns the detected calamity or null. The first call only remembers the values.
     */
    public Event check(double[] today) {
        if (previous == null) {
            previous = today.clone();
            return null;
        }
        double[] change = new double[SimConstants.INDICATOR_COUNT];
        for (int i = 0; i < change.length; i++) {
            change[i] = (today[i] - previous[i]) / SimConstants.range(i);
        }
        previous = today.clone();

        if (suppressDaysLeft > 0) {
            suppressDaysLeft--;
            return null;
        }

        Event best = better(null, evaluate(Type.DROUGHT, change, SimConstants.TEMP, +1,
                new int[]{SimConstants.WATER, SimConstants.HUMIDITY}, new int[]{-1, -1}));
        best = better(best, evaluate(Type.FLOOD, change, SimConstants.WATER, +1,
                new int[]{SimConstants.HUMIDITY}, new int[]{+1}));
        best = better(best, evaluate(Type.FLASH_FREEZE, change, SimConstants.TEMP, -1,
                new int[]{SimConstants.HUMIDITY}, new int[]{-1}));

        if (best != null) {
            yearCounts[best.type.ordinal()]++;
            suppressDaysLeft = SimConstants.SUPPRESS_DAYS;
        }
        return best;
    }

    /** closes the year: the counts of this year move into the track record (last N years) */
    public void endYear() {
        pastYears.addLast(yearCounts.clone());
        while (pastYears.size() > SimConstants.PROBABILITY_WINDOW_YEARS) {
            pastYears.removeFirst();
        }
        for (int i = 0; i < yearCounts.length; i++) {
            yearCounts[i] = 0;
        }
    }

    /**
     * Estimated probability that the NEXT calamity is of the given type, from the track record of the last years.
     * Smoothed (+1 per type), so the three probabilities always sum to 1, also without any calamity so far.
     */
    public double probability(Type type) {
        double mine = 0.0;
        double total = 0.0;
        for (int[] year : pastYears) {
            for (int i = 0; i < year.length; i++) {
                total += year[i];
            }
            mine += year[type.ordinal()];
        }
        return (mine + 1.0) / (total + Type.values().length);
    }

    private static Event evaluate(Type type, double[] change, int primary, int primaryDirection,
                                  int[] secondary, int[] secondaryDirection) {
        double primaryMove = primaryDirection * change[primary];
        if (primaryMove < SimConstants.SPIKE_THRESHOLD) {
            return null;
        }
        double sum = primaryMove;
        int matched = 1;
        for (int k = 0; k < secondary.length; k++) {
            double move = secondaryDirection[k] * change[secondary[k]];
            if (move >= SimConstants.SPIKE_THRESHOLD) {
                sum += move;
                matched++;
            }
        }
        if (matched < 2) {
            return null;
        }
        double severity = SimConstants.clamp(sum / matched / SimConstants.NOMINAL_SHOCK,
                SimConstants.SEVERITY_MIN, SimConstants.SEVERITY_MAX);
        return new Event(type, severity, sum);
    }

    private static Event better(Event a, Event b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return b.score > a.score ? b : a;
    }
}
