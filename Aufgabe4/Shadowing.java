package Aufgabe4;

/**
 * Invariant: 0 < transparency() <= 1.
 * Invariant Inherited: cooler() > 0, warmer() >= 0, duration() >= 1, annualCosts() >= 0, area() describes a local human-occupancy space.
 * Server-History-Constraint: transparency() constant.
 * Server-History-Constraint Inherited: duration(),cooler() constant.
 * Client-History-Constraint: none.
 */

public interface Shadowing extends PermanentAction, Cooling {

    /**
     * Precondition: none.
     * Postcondition: returns the fraction of direct sunlight that is blocked.
     */

    double transparency();
}
