package Aufgabe3;

/**
 * NOTE: Part: Vishwas (climate updation)
 * STYLE: procedural. A module with one static pure function, no object is created (private constructor).
 * Yearly update of the climate coefficient A from the CO2 level of the year and the long-term measures:
 * A_next = max(A_MIN, A * (A_GROWTH_BASE + A_CO2_FACTOR * (CO2mean/CO2_REF - 1) - mitigation))
 * Higher CO2 than the reference makes A grow faster, mitigation (sum of the effects of the long-term measures)
 * slows or reverses the growth.
 */
public final class ClimateUpdater {
    private ClimateUpdater() {
    }

    // GOOD (procedural #2): control flow and data flow are trivial. The result depends only on the three
    // parameters and constants of SimConstants, nothing is read from or written to global state, so no alias is possible
    // and the caller (BudgetSimulation) writes the result back to the Climate object itself.
    /**
     * precondition: a >= SimConstants.A_MIN, co2Mean > 0, mitigation >= 0.
     * postcondition: result >= SimConstants.A_MIN. No state is changed.
     */
    public static double nextA(double a, double co2Mean, double mitigation) {
        double factor = SimConstants.A_GROWTH_BASE
                + SimConstants.A_CO2_FACTOR * (co2Mean / SimConstants.CO2_REF - 1.0)
                - mitigation;
        return Math.max(SimConstants.A_MIN, a * factor);
    }
}
