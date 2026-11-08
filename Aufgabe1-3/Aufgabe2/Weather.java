import java.util.List;
import java.util.Random;

/**
 * Part - Dhananjay Sharma
 * AI use: Claude chat (conversation mode) was used to translate the exercise and the script to English.
 * The project structure was discussed with the group members before taking help from AI. AI Coding help for nextDay and normalValue methods
 *
 * Weather of one location (the same object that Location contains). It have 7 indicators:
 * top temperature, water reserve, CO2, humidity, fine dust, cloudiness and wind speed.
 * The indicators are stored in an array, the positions are the index constants of SimConstants,
 * so the simulation - TeamAdapter and Weather use the same numbering.
 *
 * A simulation year has SimConstants.MONTHS_PER_YEAR * DAYS_PER_MONTH days (90 days). We see them as the
 * summer season (June to August), like the temperature range 15 to 37C.
 *
 * Modularization units inside this class:
 *  Module part (static): the fixed rules of every indicator (normal value, drift,..) and the
 *  static helpers that only work with their parameters.
 *  Object part: the values of the current day, the day counter, nextDay(), changeIndicator() and the getters.
 *
 * Responsibility: create the next day from the last days and the climate coefficient, give the current
 * values with getters and let calamities change single indicators. It does not know the budget,
 * the measures or how many years the simulation runs, the simulation owns the day loop.
 *
 * Changes compared to Aufgabe 1:
 *  - 5 new indicators besides temperature and water.
 *  - Gaussian drift and pull back to normal instead of uniform random and a hard border,
 *    the last 10 days are taken into account.
 *  - temperature follows real summer values of Vienna, CO2 follows the real yearly trend,
 *    all numbers are in one place and easy to change.
 *
 * Design decision: all indicators are in this one class.
 * only needs one more setRule code. An indicator with special behaviour like the season of the temperature or
 * the trend of CO2, needs one more case in normalValue.
 */

public class Weather {
    // Module part: indicators
    private static final int N = SimConstants.INDICATOR_COUNT;
    private static final double[] NORMAL = new double[N];    // usual level without climate change
    private static final double[] CC_EFFECT = new double[N]; // change of the normal level per 1 unit of A above A_MIN
    private static final double[] SIGMA = new double[N];     // standard deviation of the daily random drift
    private static final double[] PULL = new double[N];      // 0..1, how strong the value goes back to the target each day
    private static final double[] MIN = new double[N];
    private static final double[] MAX = new double[N];

    // how much the target depends on the last days: 0 = not at all, 1 = only on the last days
    private static final double MEMORY = 0.3;

    private static final int DAYS_PER_YEAR = SimConstants.MONTHS_PER_YEAR * SimConstants.DAYS_PER_MONTH;

    /*
     * adjusting with real data: in Vienna the mean daily maximum is 24 C at the beginning
     * of June and the end of August and about 27 C in the middle of the summer (July).
     */
    private static final double TEMP_SEASON_EDGE = 24.0;
    private static final double TEMP_SEASON_PEAK = 27.0;

    /*
     * CO2 concentration rose by roughly 2.5 ppm per year in recent years and was around
     * 425 ppm in 2025. One simulation year has DAYS_PER_YEAR days
     */
    private static final double CO2_TREND_PER_DAY = 2.5 / DAYS_PER_YEAR;

    /*
     * The ranges are as INDICATOR_MIN /INDICATOR_MAX in SimConstants
     * Sigma and pull are chosen so that a normal day and the recovery after a
     * calamity change an indicator by less than SimConstants.SPIKE_THRESHOLD (15 % of the range), otherwise the
     * simulation would see a calamity where there is none
     */
    static {
        //      index                    normal  ccEffect sigma  pull   min     max
        setRule(SimConstants.TEMP,        0.0,   2.0,     0.7,   0.15,   15.0,  37.0);  // C, normal comes from the season
        setRule(SimConstants.WATER,       0.0,  -1.0,     0.1,   0.02,   -5.0,   5.0);  // deviation, negative = shortage
        setRule(SimConstants.CO2,       425.0,   0.0,     1.0,   0.3,   350.0, 800.0);  // ppm, CO2 changes the climate, not the other way
        setRule(SimConstants.HUMIDITY,   65.0,  -5.0,     3.0,   0.12,   10.0, 100.0);  // percent
        setRule(SimConstants.FINE_DUST,  20.0,   0.0,     5.0,   0.4,     5.0, 150.0);  // PM10 in micrograms per m3
        setRule(SimConstants.CLOUDINESS, 45.0,  -5.0,     3.5,   0.3,     0.0, 100.0);  // percent of the sky
        setRule(SimConstants.WINDSPEED,  12.0,   0.0,     3.0,   0.4,     0.0, 100.0);  // km/h
    }

    /** stores the rules of one indicator in the static arrays */
    private static void setRule(int i, double normal, double ccEffect, double sigma, double pull,
                                double min, double max) {
        NORMAL[i] = normal;
        CC_EFFECT[i] = ccEffect;
        SIGMA[i] = sigma;
        PULL[i] = pull;
        MIN[i] = min;
        MAX[i] = max;
    }

    // Object part: the current weather

    /*
     * The values of the current day are private. Other classes cannot change them directly,
     * they only send messages (nextDay, changeIndicator, getters) and the Weather object
     * decides itself how its state changes. State and behaviour are together in one object.
     */
    private final double[] values = new double[N];
    private final Random random;
    private int dayCount = 0;

    /** creates the weather with a shared random generator and picks start values near the normal values */
    public Weather(Random random) {
        this.random = random;
        for (int i = 0; i < N; i++) {
            double start = normalValue(i, 0, SimConstants.A_MIN) + SIGMA[i] * random.nextGaussian();
            values[i] = SimConstants.clamp(start, MIN[i], MAX[i]);
        }}

    /**
     * Moves the weather to the next day. last10Days are the last 0 - 10 days (oldest first,
     * with SimConstants indexes), climateCoefficient is A of the climate of the location (A_MIN = no change).
     *
     * How one day works for every indicator:
     *  1. target = mostly the normal value of the day, partly the mean of the last days (weather "remembers").
     *  2. the value is pulled towards the target, the further away the stronger the pull, so there are no
     *     sudden jumps at a hard border like in Aufgabe 1 (mean reversion). This also brings the weather back
     *     to normal slowly after a calamity.
     *  3. a random drift is added, normally distributed (Gaussian) instead of uniform, so small changes are
     *     common and big changes are rare, like in nature.
     *  4. the range is only enforced as a safety net at the end.
     *
     * simulation gives plain data, a list of double arrays, and the rules are read from
     * the static tables. The result is stored in the private state of this object, which is
     * only reachable through its methods.
     */

    public void nextDay(List<double[]> last10Days, double climateCoefficient) {
        dayCount++;
        for (int i = 0; i < N; i++) {
            double target = normalValue(i, dayCount, climateCoefficient);
            double recentMean = averageOf(last10Days, i);
            if (!Double.isNaN(recentMean)) {
                target = (1 - MEMORY) * target + MEMORY * recentMean;
            }
            double drift = SIGMA[i] * random.nextGaussian();
            values[i] = SimConstants.clamp(values[i] + PULL[i] * (target - values[i]) + drift, MIN[i], MAX[i]);
        }
    }

    /**
     * Sudden change of one indicator, used by calamities like drought: temperature up, water down.
     * index is a SimConstants index, delta positive = up, negative = down. The range is still kept.
     * After a calamity, nextDay pulls the value slowly back to normal.
     */
    public void changeIndicator(int index, double delta) {
        values[index] = SimConstants.clamp(values[index] + delta, MIN[index], MAX[index]);
    }

    /** returns the value of one indicator by its SimConstants index */
    public double getValue(int index) {
        return values[index];
    }
    /** daily top temperature in degrees Celsius */
    public double getTemp() {
        return values[SimConstants.TEMP];
    }
    /** water reserve as deviation from the usual level, negative means shortage */
    public double getWater() {
        return values[SimConstants.WATER];
    }
    /** CO2 concentration in ppm */
    public double getCo2() {
        return values[SimConstants.CO2];
    }
    /** relative humidity in percent */
    public double getHumidity() {
        return values[SimConstants.HUMIDITY];
    }
    /** fine dust (PM10) in micrograms per cubic metre */
    public double getFineDust() {
        return values[SimConstants.FINE_DUST];
    }
    /** cloudiness in percent of the sky */
    public double getCloudiness() {
        return values[SimConstants.CLOUDINESS];
    }
    /** wind speed in km/h */
    public double getWindspeed() {
        return values[SimConstants.WINDSPEED];
    }

    // Module part: helper

    /**
     * Normal value of indicator i on this day, shifted by the climate coefficient A, no shift at A = A_MIN.
     * Temperature follows the summer: lowest at the beginning and the end of the season, highest in the middle.
     * CO2 rises slowly every day, all other indicators have a fixed normal level.
     *
     * The special cases are chosen with if/else on the index, the procedure works only
     * with its parameters and the static tables, no object is asked.
     */
    private static double normalValue(int i, int dayCount, double climateCoefficient) {
        double normal = NORMAL[i];
        if (i == SimConstants.TEMP) {
            int dayOfSeason = dayCount % DAYS_PER_YEAR;
            double hump = Math.sin(Math.PI * dayOfSeason / DAYS_PER_YEAR);  // 0 at the edges, 1 in the middle
            normal = TEMP_SEASON_EDGE + (TEMP_SEASON_PEAK - TEMP_SEASON_EDGE) * hump;
        } else if (i == SimConstants.CO2) {
            normal = NORMAL[i] + CO2_TREND_PER_DAY * dayCount;}
        return normal + CC_EFFECT[i] * (climateCoefficient - SimConstants.A_MIN);
    }

    /**
     * mean of one indicator over the given days, Double.NaN if there are no days
     *
     * A simple loop over plain arrays, the data structure is fully visible to the procedure
     */
    private static double averageOf(List<double[]> days, int index) {
        if (days == null || days.isEmpty()) {
            return Double.NaN;
        }
        double sum = 0;
        for (double[] day : days) {
            sum += day[index];
        } return sum / days.size();
    }
}