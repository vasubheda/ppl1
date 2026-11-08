package Aufgabe2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Part: Vishwas (simulation, medium-term measures).
 *
 * Implements the "Indicator -> medium-term measure" map of the Public Acceptance Logic:
 *   Temp       -> shading systems (+) / naturally cooled buildings (++) / district cooling (+++)
 *   Water      -> water reservoir
 *   CO2        -> healthcare infrastructure
 *   Fine dust  -> healthcare infrastructure
 *   Humidity, Cloudiness, Windspeed -> festival (nature can not be influenced, so people are made happy)
 *
 * Flow of one month (the PA object calculated the honest PA before, this class only improves it):
 *  1. the total PA drop is split equally across the causes
 *  2. for Temp the cheapest tier whose gain covers the drop is chosen (else the strongest one);
 *     if it is not affordable, it falls back to a cheaper tier
 *  3. causes are served in order of best gain per cost (the "weight" of the measure) until the allowance is empty
 *  4. the result contains the total PA gain, the simulation applies it to the PA object
 *
 * Modularization unit: module-like helper with no state between months (the money state is in BudgetCalculator).
 */
public class MediumTermPlanner {

    private static final class Measure {
        final String name;
        final double cost;
        final double gain;
        final int[] indicators;     // the indicators this measure is linked to (for the Location's infrastructure)

        Measure(String name, double cost, double gain, int... indicators) {
            this.name = name;
            this.cost = cost;
            this.gain = gain;
            this.indicators = indicators;
        }

        double weight() {
            return gain / cost;
        }
    }

    private static final Measure FESTIVAL = new Measure("Festival", SimConstants.FESTIVAL_COST, SimConstants.FESTIVAL_GAIN,
            SimConstants.HUMIDITY, SimConstants.CLOUDINESS, SimConstants.WINDSPEED);
    private static final Measure SHADING = new Measure("Promoting shading systems", SimConstants.SHADING_COST, SimConstants.SHADING_GAIN,
            SimConstants.TEMP);
    private static final Measure NATURAL = new Measure("Naturally cooled buildings", SimConstants.NATURAL_COOLING_COST, SimConstants.NATURAL_COOLING_GAIN,
            SimConstants.TEMP);
    private static final Measure DISTRICT = new Measure("Building district cooling systems", SimConstants.DISTRICT_COOLING_COST, SimConstants.DISTRICT_COOLING_GAIN,
            SimConstants.TEMP);
    private static final Measure RESERVOIR = new Measure("Water reservoir", SimConstants.RESERVOIR_COST, SimConstants.RESERVOIR_GAIN,
            SimConstants.WATER);
    private static final Measure HEALTHCARE = new Measure("Healthcare infrastructure", SimConstants.HEALTHCARE_COST, SimConstants.HEALTHCARE_GAIN,
            SimConstants.CO2, SimConstants.FINE_DUST);
    private static final Measure[] ALL = {FESTIVAL, SHADING, NATURAL, DISTRICT, RESERVOIR, HEALTHCARE};

    /** the indicators (SimConstants.TEMP ...) a bought measure is linked to, empty if the name is unknown */
    public static int[] indicatorsOf(String measureName) {
        for (Measure m : ALL) {
            if (m.name.equals(measureName)) {
                return m.indicators.clone();
            }
        }
        return new int[0];
    }

    /** result of one month: money spent, PA gain to apply and what was bought (name -> count) */
    public static final class Result {
        private double spent;
        private double gain;
        private final Map<String, Integer> purchases = new LinkedHashMap<>();

        public double getSpent() {
            return spent;
        }

        public double getGain() {
            return gain;
        }

        public Map<String, Integer> getPurchases() {
            return purchases;
        }
    }

    /**
     * @param paDelta         change of PA caused by the indicators this month (negative = PA got lower)
     * @param paValue         the honest PA after the update (0-100)
     * @param causeIndicators indicator indexes (SimConstants.TEMP ...) whose threshold was violated
     * @param budget          provides the monthly allowance and takes the payments
     *
     * Nothing is bought without a cause or when PA is already at the maximum. If PA could not drop any further
     * (already at 0, delta = 0) but causes exist, the cheapest measure of each cause is bought.
     */
    public Result act(double paDelta, double paValue, List<Integer> causeIndicators, BudgetCalculator budget) {
        Result result = new Result();
        Set<Integer> causes = new LinkedHashSet<>(causeIndicators);
        if (causes.isEmpty() || paValue >= 100.0) {
            return result;
        }
        double drop = Math.max(0.0, -paDelta);
        double share = drop / causes.size();

        // serve the causes with the best gain per cost first
        List<Integer> ordered = new ArrayList<>(causes);
        ordered.sort((x, y) -> Double.compare(options(y)[wanted(options(y), share)].weight(),
                options(x)[wanted(options(x), share)].weight()));

        for (int indicator : ordered) {
            Measure[] options = options(indicator);
            for (int i = wanted(options, share); i >= 0; i--) {     // fall back to cheaper tiers if needed
                if (budget.trySpendMedium(options[i].cost)) {
                    result.spent += options[i].cost;
                    result.gain += options[i].gain;
                    result.purchases.merge(options[i].name, 1, Integer::sum);
                    break;
                }
            }
        }
        return result;
    }

    /** the measures (cheapest first) that help against a violated indicator */
    private static Measure[] options(int indicator) {
        switch (indicator) {
            case SimConstants.TEMP:
                return new Measure[]{SHADING, NATURAL, DISTRICT};
            case SimConstants.WATER:
                return new Measure[]{RESERVOIR};
            case SimConstants.CO2:
            case SimConstants.FINE_DUST:
                return new Measure[]{HEALTHCARE};
            default:    // humidity, cloudiness, windspeed
                return new Measure[]{FESTIVAL};
        }
    }

    /** index of the cheapest option whose gain covers the PA drop, the strongest one if none does */
    private static int wanted(Measure[] options, double drop) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].gain >= drop) {
                return i;
            }
        }
        return options.length - 1;
    }
}
