package Aufgabe2;

/**
 * Part: Vishwas (climate update).
 *
 * Yearly update of the climate coefficient A, depending on the CO2 level of the year and on the long-term measures.
 *   A_next = max(A_MIN, A * (A_GROWTH_BASE + A_CO2_FACTOR * (CO2mean / CO2_REF - 1) - mitigation))
 * - higher CO2 than the reference makes A grow faster,
 * - mitigation (sum of the effects of the long-term measures) slows or reverses the growth.
 *
 * Modularization unit: module (pure function, no state). The state A itself is held by Climate.
 */
public final class ClimateUpdater {
    private ClimateUpdater() {
    }

    /**
     * @param a          climate coefficient of the year that just ended
     * @param co2Mean    mean CO2 value of that year
     * @param mitigation total yearly reduction of the growth factor from the long-term measures (>= 0)
     * @return the climate coefficient for the next year (never below A_MIN)
     */
    public static double nextA(double a, double co2Mean, double mitigation) {
        double factor = SimConstants.A_GROWTH_BASE
                + SimConstants.A_CO2_FACTOR * (co2Mean / SimConstants.CO2_REF - 1.0)
                - mitigation;
        return Math.max(SimConstants.A_MIN, a * factor);
    }
}
