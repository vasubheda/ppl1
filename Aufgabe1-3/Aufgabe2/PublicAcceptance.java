import java.util.ArrayList;
import java.util.List;

/**
 * Part: Dhananjay Sharma
 *
 * AI use: Claude chat (conversation mode) was used to translate the exercise and the script to English
 * The design plan was discussed with the group members before coding.
 * update method and penaltyOf method code is done using AI.
 *
 * Public acceptance(PA) of one location, a value between 0 (nobody accepts the measures) and 100 (everybody
 * accepts them). It is the same object that Location contains.
 *
 * How PA changes:
 *  - every month the simulation gives the monthly means of the 7 indicators. If an indicator is over its
 *    threshold (or under it, for the water reserve), people are unhappy and PA will lower. The result also
 *    says which indicators were the cause, e.g PA = -20, causes = (Temp, CO2).
 *  - when medium-term measures are taken (festival, shading,..), PA gets higher - increase.
 *  - measures that people see as negative (e.g. a CO2 tax) can lower PA - decrease.
 *
 * Refinement compared to a fixed penalty: the penalty grows with how far the threshold is exceeded, a small
 * exceedance costs only a little PA, a big one costs much more (continuous, no jumps).
 *
 * Responsibility: know the current Public Acceptance and calculate how the indicators change it. It does not know the
 * measures, the budget or the weather object, it only gets plain numbers from the simulation.
 */
public class PublicAcceptance {
    // Module part: thresholds and penalties
    private static final double PA_MIN = 0.0;
    private static final double PA_MAX = 100.0;
    private static final double START_PA = 50.0;

    /*
     * Threshold of every indicator for the monthly mean (index = SimConstants index).
     * NaN means: people do not care about this indicator.
     * Fine dust: the EU limit value for PM10 is 40 micrograms per m3 as yearly mean
     */
    private static final double[] THRESHOLD = new double[SimConstants.INDICATOR_COUNT];
    // true = it is bad when the value is ABOVE the threshold, false = bad when it is BELOW (water shortage)
    private static final boolean[] BAD_WHEN_HIGH = new boolean[SimConstants.INDICATOR_COUNT];

    static {
        setThreshold(SimConstants.TEMP, 30.0, true);   // hot month, normal summer is 24 to 27C
        setThreshold(SimConstants.WATER, -1.5, false);   // clear water shortage
        setThreshold(SimConstants.CO2, 450.0, true);    // ppm
        setThreshold(SimConstants.HUMIDITY, 85.0, true);  // muggy, unpleasant
        setThreshold(SimConstants.FINE_DUST, 40.0, true); // EU limit value
        setThreshold(SimConstants.CLOUDINESS, Double.NaN, true);
        setThreshold(SimConstants.WINDSPEED, 40.0, true); // storms, km/h
    }

    // penalty = LINEAR * e + QUADRATIC * e*e, where e = exceedance as share of indicator range
    private static final double LINEAR = 20.0;
    private static final double QUADRATIC = 200.0;

    /** stores the threshold of one indicator in the static arrays */
    private static void setThreshold(int index, double threshold, boolean badWhenHigh) {
        THRESHOLD[index] = threshold;
        BAD_WHEN_HIGH[index] = badWhenHigh;
    }

    // Object part: current public acceptance

    private double value;
    /** creates public acceptance with the start value 50 */
    public PublicAcceptance() {
        this(START_PA);
    }
    /** creates public acceptance with a chosen start value (kept inside 0 - 100) */
    public PublicAcceptance(double startValue) {
        this.value = SimConstants.clamp(startValue, PA_MIN, PA_MAX);
    }

    /**
     * Monthly update: compares the monthly means with the thresholds,
     * lowers PA by the penalties and returns the new PA, the change and the names of the causes.
     *
     * the penalties are calculated by the procedures penaltyOf and excess, which only work
     * with plain numbers and the static tables. This object then changes its own private
     * state and gives back a PAResult object.
     */
    public PAResult update(double[] monthlyMeans) {
        double before = value;
        double totalPenalty = 0.0;
        List<String> causes = new ArrayList<>();
        for (int i = 0; i < SimConstants.INDICATOR_COUNT; i++) {
            double penalty = penaltyOf(i, monthlyMeans[i]);
            if (penalty > 0) {
                totalPenalty += penalty;
                causes.add(SimConstants.INDICATOR_NAMES[i]);
            }
        }
        value = SimConstants.clamp(value - totalPenalty, PA_MIN, PA_MAX);
        return new PAResult(value, value - before, causes);
    }

    /** PA gets higher, e.g. after medium-term measures (gain >= 0) */
    public void increase(double gain) {
        value = SimConstants.clamp(value + Math.max(0.0, gain), PA_MIN, PA_MAX);
    }

    /** PA gets lower, e.g. after measures that people see as negative like a CO2 tax (loss >= 0) */
    public void decrease(double loss) {
        value = SimConstants.clamp(value - Math.max(0.0, loss), PA_MIN, PA_MAX);
    }

    /** returns current PA (0 to 100) */
    public double getValue() {
        return value;
    }

    // Module part: helper
    /**
     * Penalty (PA points) of indicator i for its monthly mean, 0 if the threshold is not violated.
     * e is the exceedance as share of the indicator range, so all indicators are comparable.
     * Small e gives a small penalty, big e a much bigger one (linear + quadratic part).
     *
     * A static procedure that reads the global tables of the module and calculates a number
     * from its parameters with if and arithmetic, no object is involved.
     */
    static double penaltyOf(int i, double mean) {
        double over = excess(i, mean);
        if (over <= 0) {
            return 0.0;
        }
        double e = over / SimConstants.range(i);
        return LINEAR * e + QUADRATIC * e * e;
    }

    /** how far mean is on the bad side of the threshold, 0 or negative if it is fine, also if no threshold
     */
    private static double excess(int i, double mean) {
        if (Double.isNaN(THRESHOLD[i])) {
            return 0.0;
        }
        if (BAD_WHEN_HIGH[i]) {
            return mean - THRESHOLD[i];
        }
        return THRESHOLD[i] - mean;
    }
}
