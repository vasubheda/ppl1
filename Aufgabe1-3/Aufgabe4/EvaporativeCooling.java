package Aufgabe4;

/**
 * Invariant: warmer() == 0, consumption() > 0.
 * Invariant Inherited: cooler() > 0, area() limited.
 * Server-History-Constraint: consumption() constant.
 * Server-History-Constraint Inherited: cooler(), warmer() constant.
 * Client-History-Constraint: none.
 */

public interface EvaporativeCooling extends Cooling {

    /**
     * Precondition: none.
     * Postcondition: returns the avg liters of water evaporated per day.
     */

    double consumption();
}
