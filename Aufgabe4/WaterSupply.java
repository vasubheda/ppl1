/*
 * WaterSupply is measure to improve or having drinking-water supply of a limited area.
 * How fast it takes effect does not matter (hours, days or only after years).
 *
 * Invariant: the drinking-water supply of the limited area given by area() gets
 * better or more secure.
 * Invariant: how fast the measure takes effect is not fixed, so it is neither an
 * ImmediateAction nor a PermanentAction.
 * All assertions of LocalAction (and Action) hold here too,
 * area() returns a non-empty value describing the limited amount area.
 */
public interface WaterSupply extends LocalAction {
    // Postcondition: returns how many cubic metres of drinking water are secured or
    // additionally available, result > 0.
    // History constraint: the value does not change.
    double capacity();
}
