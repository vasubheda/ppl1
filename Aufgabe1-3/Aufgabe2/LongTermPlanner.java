package Aufgabe2;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part: Vishwas (simulation, long-term measures).
 *
 * Once a year the remaining budget is split on the 5 long-term measures. The score of a measure is
 *   score = 0.6 * indicator score + 0.4 * calamity risk
 * Indicator score (from the yearly mean of the linked indicator, normalised 0..1):
 *   CO2 tax        - need:      CO2 high
 *   Solar          - potential: cloudiness low
 *   Windmills      - potential: windspeed high
 *   Turbines/Dams  - potential: water level high
 *   Heating infra  - need:      temperature low
 * Calamity risk (track record of the last years, see CalamityDetector.probability):
 *   drought -> dams and solar (half each), flood -> dams, flash freeze -> heating infra
 * The remainder is split proportionally to the scores. The money of a measure builds up its "level"
 * (invested / cost per level), scaled by the public acceptance effectiveness (0.5 + PA/200).
 * The effect on the climate is mitigation = sum m_i * L * (1 - exp(-level_i / L)) (diminishing returns),
 * which {@link ClimateUpdater} uses to slow the growth of A.
 *
 * Modularization unit: object (the levels of the measures are built up over the whole run of a location).
 */
public class LongTermPlanner {
    private static final int COUNT = SimConstants.LONG_NAMES.length;
    private final double[] invested = new double[COUNT];
    private final double[] level = new double[COUNT];

    /**
     * @param remainder  money left for the long-term measures this year
     * @param yearMeans  yearly means of the 7 indicators
     * @param detector   calamity track record
     * @param pa         public acceptance (0-100) at the end of the year
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

    /** total reduction of the yearly growth factor of A that the long-term measures built up so far */
    public double mitigation() {
        double sum = 0.0;
        double sat = SimConstants.LONG_SATURATION_LEVEL;
        for (int i = 0; i < COUNT; i++) {
            sum += SimConstants.LONG_MITIGATION_PER_LEVEL[i] * sat * (1.0 - Math.exp(-level[i] / sat));
        }
        return sum;
    }

    public double[] getLevels() {
        return level.clone();
    }
}
