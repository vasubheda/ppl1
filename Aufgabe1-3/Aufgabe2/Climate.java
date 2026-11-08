/**
 * Originally done by Dhananjay Sharma (Assignment 1, see AI-use declaration there).
 * Assignment 2 change (Vishwas, "climate update"): the update logic (cause shares, 10 year window) was removed,
 * the yearly update of A is now calculated in {@link ClimateUpdater} (based on CO2 and long-term measures) and
 * written back with {@link #setA(double)} by the simulation.
 *
 * Holds the climate coefficient A of a location.
 * NOTE: the Calamity sub-object of the Climate class (diagram) is added by the calamity owner.
 */
public class Climate {
    private double a;

    /** Takes the initial climate coefficient A. */
    public Climate(double startA) {
        this.a = startA;
    }

    /** gives back the climate coefficient A */
    public double getA() {
        return a;
    }

    /** replaces A; called once per year by the simulation after the yearly update */
    public void setA(double a) {
        this.a = a;
    }
}
