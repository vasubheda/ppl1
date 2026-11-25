package Aufgabe3;

import java.util.ArrayDeque;
import java.util.Deque;

// NOTE: Part - Vishwas
// STYLE: object-oriented. The object remembers the weather of yesterday and the calamity track record in private fields.
// STYLE : object-oriented. The object remembers the weather of yesterday and the calamity track record in private fields.
// The static helpers evaluate and better are procedural
// BAD (OO #3, class cohesion): this class has 2 jobs, 1 is detecting a spike from two consecutive days (check)
// and 2 is keeping the multi-year statistics (yearCounts, pastYears, endYear, prob.) for LongTermPlanner.
// They only share the detected type. This came from adding the statistics where the events were detected.
// Better: a separate CalamityStatistics class that gets each Event from the simulation; then the detector would only need "previous", and LongTermPlanner would not depend on the detector.
/**
* Detects calamities from daily weather.
* A calamity changes several indicators significantly, from one day to the next (a spike), all the other dynamics like gradual recovery, normal increments,are not considered spikes. (change per day below SPIKE_THRESHOLD of the indicator range).
*
* How the weather values are changed according to the calamities :
* 1. Drought : Temp up, Water down, Humidity down (all other indicators stay normal)
* 2. Flood : Water up, Humidity up (all others stay normal)
* 3. Flash Freeze : Temp down, Humidity down (all others stay normal)
*
* This also keeps the calamity track record of the last years, which the long-term measures use to estimate which calamity is probable in the future.
*
 */
public class CalamityDetector {

    public enum Type {DROUGHT, FLOOD, FLASH_FREEZE}

    // Gives the detected calamity type with its severity
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
     * precondition: today.length == SimConstants.INDICATOR_COUNT, called once per simulated day in order.
     * postcondition: returns null or an Event whose severity is in [SEVERITY_MIN, SEVERITY_MAX].
     * A detected event is counted in this year's statistics.
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

    /**
     * precondition: called once per year after the last check() of that year.
     * postcondition: this year's counts are in the track record (at most PROBABILITY_WINDOW_YEARS years kept), the counters of the new year are 0.
     */
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
     * precondition: type != null.
     * postcondition: 0 < result < 1 (smoothed by +1 per type), the three types together sum to 1. No state is changed.
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
