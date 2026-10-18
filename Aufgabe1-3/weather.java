import java.util.Random;

/**
 * Done by Dhananjay Sharma
 * AI use - Cluade chat(conversion mode) is used starting from the translating the excercise and course Script to English till the end untill
 * I fully understand theexercise. I shared my plan which was discussed with grp members before.
 * AI suggested the enforce range method to use.
 *
 * Weather have two variables: daily high temperature and water reserve.
 *
 * Modularization units inside this weather class:
 *  Module part is holding the fixed rules, the ranges and limit that never changes - constants (value ranges, maximum drift) and the
 *  static helper EnforceRange() which enforces the range.
 *  Object part holds the current days values and the behaviour that updates over time - the instance fields topTemperature and
 *  waterReserve together with startNewYear(), nextDay() and the getters.
 *
 * Responsibility : pick a random start value from a fixed range, draw a random daily drift, apply it to the previous normal weather (PNW) and
 * expose the current day's values via getters. The class does not know how many
 * days a year has, nor A, CW (changed weather), M or the budgets; the simulation owns the day
 * loop and combines those.
 */
public class Weather {

    // Range of daily high temperature in degrees Celcius and maximum daily drift
    private static final double TEMP_MIN = 15.0;
    private static final double TEMP_MAX = 37.0;
    private static final double TEMP_MAX_DRIFT = 1.2;

    // Water reserve as deviation from the usual level and water reserve in negative means shortage
    private static final double WATER_MIN = -5.0;
    private static final double WATER_MAX = 5.0;
    private static final double WATER_MAX_DRIFT = 0.1;
    private final Random random;
    private double TopTemp;
    private double WaterReserve;

    /** creates the weather with a shared random generator and picks the first start values which will be
     * used for the two variables. */
    public Weather(Random random) {
        this.random = random;
        startNewYear();
    }

    /** it chooses the start temperature and water reserve randomly from the fixed range */
    public void startNewYear() {
        TopTemp = RandomBetween(TEMP_MIN, TEMP_MAX);
        WaterReserve = RandomBetween(WATER_MIN, WATER_MAX);
    }

    /** Moves to the next day. The drift is a random change between -maxDrift and +maxDrift for each value,
     * it is applied to the previous normal weather (PNW) */
    public void nextDay() {
        double pnwTemp = TopTemp;
        double pnwWaterReserve = WaterReserve;
        double temperatureDrift = RandomBetween(-TEMP_MAX_DRIFT, TEMP_MAX_DRIFT);
        double waterDrift = RandomBetween(-WATER_MAX_DRIFT, WATER_MAX_DRIFT);
        TopTemp = EnforceRange(pnwTemp +temperatureDrift, TEMP_MIN, TEMP_MAX);
        WaterReserve = EnforceRange( pnwWaterReserve + waterDrift, WATER_MIN, WATER_MAX);
    }
    /** Returns the current day's top temperature and will be used for the simulation */
    public double getTopTemp() {
        return TopTemp;
    }
    /** Returns the current day's water reserve and will be used for silmulation */
    public double getWaterReserve() {
        return WaterReserve;
    }
    /** it returns a uniformly distributed random value between lo and hi */
    private double RandomBetween(double lo, double hi) {
        return lo + random.nextDouble() * (hi - lo);
    }

    /** keeps a value inside [lo, hi] so that the normal weather always stays in the same range */
    private static double EnforceRange(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}