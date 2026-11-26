/**
 * Part : Vishwas
 * This is a measure that binds CO2 for a long time, so the carbon is no longer part of CO2 in the open air.
 *
 * Invariant: duration() >= 10, the CO2 stays bound for at least a decade (stronger than PermanentAction).
 * Invariant: the effect is worldwide (there is only one air), so it is never a local measure.
 * Invariant Inherited: annualCosts() >= 0 (PermanentAction), costs() >= 0 (Action).
 * Server-History-Constraint: reduction() constant.
 * Server-History-Constraint Inherited: duration(), annualCosts() constant.
 * Client-History-Constraint: none.
 */
public interface CO2Binding extends PermanentAction {

    /**
     * Precondition: none.
     * Postcondition: returns the tonnes of CO2 bound by the measure, result > 0.
     */
    double reduction();
}
