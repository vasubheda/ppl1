package Aufgabe2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Part: Dhananjay Sharma.
 *
 * The result of one monthly update of the public acceptance: the new PA value, how much it changed and which
 * indicators caused the drop.
 * The cause names are the names of SimConstants.INDICATOR_NAMES
 *
 * Values are set once in the constructor and can only be read with getters,
 * nobody can change the result afterwards (data hiding).
 *
 * STYLE: object-oriented
 *
 * Invariant: the object is immutable, every getter always returns the same content.
 *
 * GOOD: Weak coupling, final fields and an unmodifiable copy of the list.
 *
 * Precondition: causes is not null.
 * Postcondition: values stored as given, getCauses is an unmodifiable copy.
 */
public class PAResult {
    private final double pa;
    private final double delta;
    private final List<String> causes;

    public PAResult(double pa, double delta, List<String> causes) {
        this.pa = pa;
        this.delta = delta;
        this.causes = Collections.unmodifiableList(new ArrayList<>(causes));
    }

    /** Public Acceptance after the update (0 to 100) */
    public double getPa() {
        return pa;
    }

    /** change of the public acceptance in this update, negative = got lower */
    public double getDelta() {
        return delta;
    }

    /** names of the indicators whose threshold was violated, empty if none */
    public List<String> getCauses() {
        return causes;
    }
}
