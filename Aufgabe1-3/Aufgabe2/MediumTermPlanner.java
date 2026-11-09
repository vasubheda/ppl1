package Aufgabe2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* Part : Vishwas (simulation, medium-term measures)
* This maps the indicator states with the medium-term Measure
*
* Temp -> shading systems(+) / naturally cooled buildings(++)/ district cooling (+++), the increasing "+" indicate the helpfulness of the investments, the severe the temperature, the more helpful the higher "+".
* Water -> water reservoir
* CO2 -> healthcare Infrastructure
* Fine dust -> healthcare Infrastructure
* Humidity, Cloudiness, Windspeed -> festival (nature is not in our hands, so people are made happy with festivals)
*
* The flow
* - The PA object calculated the honest PA before, this class only improves it.
* - The total PA drop is split equally across the causes
* - For temp, cheapest tier whose gain covers the drop, is chosen (else the strongest one), if it is not affordable, it falls back to a cheaper tier.
* - Causes are catered in order of the weight of the measure, until the allowance is empty.
* - The result contains the total PA gain, the simulation applies it to the PA object
*
 */
public class MediumTermPlanner {

    private static final class Measure {
        final String name;
        final double cost;
        final double gain;
        final int[] indicators;     // the indicators this measure is linked to

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

    // The indicators, a bought measure is linked to.
    public static int[] indicatorsOf(String measureName) {
        for (Measure m : ALL) {
            if (m.name.equals(measureName)) {
                return m.indicators.clone();
            }
        }
        return new int[0];
    }

    // contains the result of the month, i.e. the money spent, the PA gain to apply and the details of what was bought (name->count)
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
     * @param paDelta         this is the change of PA caused by the indicators this month (negative = public acceptance got lower)
     * @param paValue         the honest PA value after the update(range 0-100)
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

        // cater the causes with the best gain per cost first
        List<Integer> ordered = new ArrayList<>(causes);
        ordered.sort((x, y) -> Double.compare(options(y)[wanted(options(y), share)].weight(),
                options(x)[wanted(options(x), share)].weight()));

        for (int indicator : ordered) {
            Measure[] options = options(indicator);
            for (int i = wanted(options, share); i >= 0; i--) {     // fallback to cheaper tiers if needed
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

    // The measures that help against a violated indicator, order - cheapest first.
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

    // Index of the cheapest option whose gain covers the PA drop, and the strongest one if none covers the drop.
    private static int wanted(Measure[] options, double drop) {
        for (int i = 0; i < options.length; i++) {
            if (options[i].gain >= drop) {
                return i;
            }
        }
        return options.length - 1;
    }
}
