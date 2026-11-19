package Aufgabe3;

import java.util.Map;

/*
    Modulisationunit of the class:
    Object: all class variables are object because every built infrastructure is its own object of a location

    Abstraktion: This class is responsible for representing one built structural measure and which indicators
    it influences. It only stores this information and gives it back, it does not change any indicator itself.

    STYLE: object-oriented. Infrastructure is a small object that hides its data. the name is private and final.
    The affected indicators are only readable through a getter. State and the methods on it live together in one object.

    Invariant: name != null and final; affectedIndicators != null (reference never reassigned, see BAD)

    GOOD(weak coupling/high cohesion): tiny object, one task (a built measure + its effect).
    knows nothing about Weather or Location its loosely coupled and reusable.
*/

/**
 * Class for one built structural measure of a location (e.g. a water reservoir, a healthcare infrastructure, a district cooling system).
 * It knows its name and which indicators it affects and by how much (indicator name -> effect, e.g. "Water" -> +3).
 * This makes the resilience of a location against natural events visible
 */

public class Infrastructure {
    final private String name;
    private Map<String, Integer> affectedIndicators;

    /**
     * creates an infrastructure with its name and the indicators it affects (indicator name -> effect)
     * precondition: name != null, affectedIndicators != null
     * postcondition: object is immutable afterward
     */

    public Infrastructure(String name, Map<String, Integer> affectedIndicators) {
        this.name = name;
        this.affectedIndicators = affectedIndicators;
    }

    public String getName() {
        return name;
    }

    /*
        BAD(encapsulation/aliasing): returns the internal map directly so a client can change the stored effects from outside.
        The field is also not final though never reassigned.
        Better: make it final and return an unmodifiable view or a copy.
    */

    /**
     * the indicators this infrastructure affects and by how much (indicator name -> effect)
     * postcondition: returns the stored map (live reference, see BAD); object unchanged.
     */

    public Map<String, Integer> getAffectedIndicators() {
        return affectedIndicators;
    }
}
