package Aufgabe4;

/*
 * Permanent Action is measure of any kind that aims at a long-lasting change of a current state.
 *
 * Invariant: the relevant effect is expected to be noticeable for about one year
 * or longer (extent and strength may change over time).
 * All assertions of Action hold here too, as it's a subtype of Action.
 */
public interface PermanentAction extends Action {
    //

    // Postcondition: returns the number of years the measure is expected to be
    // noticeable, result >= 1.
    // History constraint: the value does not change.
    int duration();


    // Postcondition: returns the estimated costs per year (every year except the first)
    // as long as the effect is noticeable, result >= 0.
    // History constraint: the value does not change.
    double annualCosts();
}
