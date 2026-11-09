package Aufgabe2;

/**
* Part : Vishwas(climate updation)
* Yearly update of the climate coefficient A, depending on the CO2 level of the year and on the long-term measures is done in this class.
* This is how we compute the next A -
* A_next = max(A_MIN, A *(A_GROWTH_BASE + A_CO2_FACTOR * (CO2mean/CO2_REF -1) -  mitigation))
* i.e., higher CO2 than the reference makes A grow faster, mitigation (the sum of the effects of the long-term measures) slows or reverses the growth
*
 */
public final class ClimateUpdater {
    private ClimateUpdater() {
    }

    public static double nextA(double a, double co2Mean, double mitigation) {
        double factor = SimConstants.A_GROWTH_BASE
                + SimConstants.A_CO2_FACTOR * (co2Mean / SimConstants.CO2_REF - 1.0)
                - mitigation;
        return Math.max(SimConstants.A_MIN, a * factor);
    }
}
