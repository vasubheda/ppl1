    /**
     * precondition: inherited: a measure may only be taken when precondition() holds, add(x) needs x!=null.
     * Invariant: cooler() > 0 cooling always lowers the temperature by a positive amount, warmer() >= 0 it may heat the near-outside (AC) or not (0) cannot be negative.
     * Invariant Inherited: area() describes a determinable non-global area.
     * Server-History-Constraint: cooler() and warmer() stay constant over the object's lifetime.
     * Client-History-Constraint: none.
    */

public interface Cooling extends LocalAction {

    /**
     * Precondition: none.
     * Postcondition: returns the avg degrees Celsius the temperature is lowered in the affected area.
     */
    
    double cooler();

    /**
     * Precondition: none.
     * Postcondition: returns the max degrees Celsius the temperature rises just outside that area.
     */

    double warmer();
}
