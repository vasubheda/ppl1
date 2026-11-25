package Aufgabe3;

import java.util.LinkedHashMap;
import java.util.Map;

// NOTE: Part: Vishwas
// STYLE: object-oriented. The planner keeps the invested money and the levels of the five measures in private arrays.
/**
* Once a year, the remaining budgt is split on the 5 long-term measures, the measures are scored by the formula -
* score = 0.6 * indicator score + 0.4 * calamity risk
* The indicator score, and how we categorized the long-term measures :
* Note that the indicator values shown below are the yearly means.
* CO2 tax       - need : CO2 High
* Heating infra - need : temperature Low
* Solar         - potential : cloudiness Low
* Windmills     - potential : windspeed High
* Turbines/Dams - potential : water level High
*
* calamity risk (track record of the last years, see CalammityDetector.probability)
*  drought -> dams and solar (half each), for flood -> dams, for flash freeze -> heating infra
* The remainder is split according to the scores.
* Then scaled by the public acceptance effectiveness (0.5 + PA/200).
*
* The effect on climate is mitigation = sum m_i * L * (1 - exp(-level_i/L)) (diminishing returns), which the ClimateUpdater uses to slow down the growth of A.
*
 */

/*
 * Class invariant: level[i] >= 0, registeredWholeLevels[i] <= floor(level[i]), all arrays have length COUNT.
 * History constraint: per year allocate() is called once, then newWholeLevels() once; mitigation() may be called at any time.
 */
public class LongTermPlanner {
    private static final int COUNT = SimConstants.LONG_NAMES.length;
    // BAD (OO #6, cohesion) : invested is only written, never read, so it is dead state.
    // level, invested and registeredWholeLevels are parallel arrays tied together by the index, and the names, costs and mitigation values for this index sit in SimConstants.
    // Better: one small LongTermMeasure object per measure.
    private final double[] invested = new double[COUNT];
    private final double[] level = new double[COUNT];
    private final int[] registeredWholeLevels = new int[COUNT];  // whole levels that are already reported to the infrastructure

    /**
     * precondition: remainder >= 0, yearMeans.length == 7, pa in [0,100], detector != null, once per year.
     * postcondition: the returned map has one entry per name in SimConstants.LONG_NAMES (same order)
     * and the sum of its values equals remainder (up to rounding), all values >= 0.
     * The levels grew by amount / cost * effectiveness.
     * @param remainder  The money left for the long term measures this year
     * @param yearMeans  7 indicator yearly means
     * @param detector   track record of the last years.. for calamity
     * @param pa         public acceptance at the end of the year (range 0-100)
     * @return how much money each long-term measure received this year (name -> amount)
     */
    public Map<String, Double> allocate(double remainder, double[] yearMeans, CalamityDetector detector, double pa) {
        double[] indicatorScore = {
                SimConstants.normalise(SimConstants.CO2, yearMeans[SimConstants.CO2]),
                1.0 - SimConstants.normalise(SimConstants.CLOUDINESS, yearMeans[SimConstants.CLOUDINESS]),
                SimConstants.normalise(SimConstants.WINDSPEED, yearMeans[SimConstants.WINDSPEED]),
                SimConstants.normalise(SimConstants.WATER, yearMeans[SimConstants.WATER]),
                1.0 - SimConstants.normalise(SimConstants.TEMP, yearMeans[SimConstants.TEMP])
        };

        double pDrought = detector.probability(CalamityDetector.Type.DROUGHT);
        double pFlood = detector.probability(CalamityDetector.Type.FLOOD);
        double pFreeze = detector.probability(CalamityDetector.Type.FLASH_FREEZE);
        double[] risk = new double[COUNT];
        risk[SimConstants.LT_DAMS] = 0.5 * pDrought + pFlood;
        risk[SimConstants.LT_SOLAR] = 0.5 * pDrought;
        risk[SimConstants.LT_HEATING] = pFreeze;

        double[] score = new double[COUNT];
        double total = 0.0;
        for (int i = 0; i < COUNT; i++) {
            score[i] = Math.max(SimConstants.LONG_SCORE_FLOOR,
                    SimConstants.LONG_WEIGHT_INDICATOR * indicatorScore[i] + SimConstants.LONG_WEIGHT_CALAMITY * risk[i]);
            total += score[i];
        }

        double effectiveness = SimConstants.paEffectiveness(pa);
        Map<String, Double> allocation = new LinkedHashMap<>();
        for (int i = 0; i < COUNT; i++) {
            double amount = remainder * score[i] / total;
            invested[i] += amount;
            level[i] += amount / SimConstants.LONG_COST_PER_LEVEL[i] * effectiveness;
            allocation.put(SimConstants.LONG_NAMES[i], amount);
        }
        return allocation;
    }

    // postcondition: result >= 0, consistently growing with the levels (diminishing returns). No state is changed.
    public double mitigation() {
        double sum = 0.0;
        double sat = SimConstants.LONG_SATURATION_LEVEL;
        for (int i = 0; i < COUNT; i++) {
            sum += SimConstants.LONG_MITIGATION_PER_LEVEL[i] * sat * (1.0 - Math.exp(-level[i] / sat));
        }
        return sum;
    }

    // BAD (OO #7, command/query mix): this looks like a query (getter) but it changes registeredWholeLevels, so calling it twice returns zeroes the second time.
    // The contract hides in a history constraint.
    // Better: the simulation keeps what it already reported, or the method is renamed to e.g. takeNewWholeLevels().
    // precondition: allocate() was called this year.
    // postcondition: gained[i] >= 0 is the number of whole levels that appeared since the last call, and they are registered, so they are not returned again.
    public int[] newWholeLevels() {
        int[] gained = new int[COUNT];
        for (int i = 0; i < COUNT; i++) {
            int whole = (int) Math.floor(level[i]);
            gained[i] = whole - registeredWholeLevels[i];
            registeredWholeLevels[i] = whole;
        }
        return gained;
    }

    public double[] getLevels() {
        return level.clone();
    }
}
