/**
 * Part : Vishwas
 * This is afforestation that creates a large, contiguous forest (never the whole world).
 * The effects grow slowly - globally by binding CO2, locally by keeping rain water and cooling through the leaves.
 *
 * Invariant: the forest is contiguous, has a minimum size and is not used to shade spaces where people stay.
 * Invariant Inherited: duration() >= 10 (CO2Binding), annualCosts() >= 0, uses plants (PlantBased).
 * Server-History-Constraint: forestationArea(), area() constant.
 * Server-History-Constraint Inherited: reduction(), duration(), annualCosts() constant.
 * Client-History-Constraint: none.
 */
public interface Forestation extends CO2Binding, PlantBased {

    /**
     * Precondition: none.
     * Postcondition: returns the directly afforested area in hectares, result > 0.
     */
    double forestationArea();

    /**
     * Precondition: none.
     * Postcondition: returns a text (not null, not empty) describing the whole contiguous forest.
     * Note: this is not LocalAction.area(), Forestation is not a local measure.
     */
    String area();
}
