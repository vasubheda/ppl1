package Aufgabe3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    Modulisationunit of the class:
    Object: all class variables are object because every location has its own log with its own records

    Abstraktion: This class is responsible for storing the recorded results of the simulation to be used in he evaluation.
    It only stores and gives back, it does not calculate anything itself.

    STYLE: object-oriented. Every log is an object with its own private collections.
    only its own methods (addYear, addMonth, the getters) fill or read them.
    State and behaviour are bundled and hidden behind the methods.

    Invariant: locationName and the three collections != null and never reassigned;
    yearlyClimateCoefficients and yearlyBudgets always have the same key set (addYear fills both together).

    History constraint: entries are only added (addYear once per year 1..100, addMonth once per month, ascending);
    nothing is removed or changed.

    GOOD(high cohesion): a pure recorder, no scoring logic.
*/

/**
 * Class to record the results of the simulation of ONE location.
 * it has to record the following things:
 * yearly: the climate coefficient A at the end of each year (year -> A) and how the yearly budget was used
 *         (year -> YearBudget: immediate, medium and long-term measures)
 * monthly: for each of the 300 months a MonthRecord (short-term measures, medium-term measures, public
 *          acceptance, number of calamities)
 * precondition: one LogInformation belongs to one location and is filled by the simulation while it runs,
 * then read by the {@link Evaluation#startEvaluation(LogInformation)}
 */

public class LogInformation {

    private String locationName;

    // Yearly:
    private Map<Integer, Double> yearlyClimateCoefficients;
    private Map<Integer, YearBudget> yearlyBudgets;

    //monthly:
    private List<MonthRecord> monthlyRecords;

    /**
     * creates an empty log with the three collections ready, to be filled by the simulation
     * precondition: locationName != null.
     * postcondition: the three collections are empty; getLocationName() == locationName
     */

    public LogInformation(String locationName) {
        this.locationName = locationName;
        yearlyClimateCoefficients = new HashMap<>();
        yearlyBudgets = new HashMap<>();
        monthlyRecords = new ArrayList<>();
    }

    /**
     * stores the result of one finished year: the climate coefficient A at the year end and the YearBudget.
     * should be used once per year. Possible values: year 1-100.
     * precondition: year in [1,100], budget != null (ascending order is the client's history constraint, not re-checked here).
     * postcondition: both maps contain year afterward; their key sets stay equal; no earlier entry changed.
     */

    public void addYear(int year, double climateCoefficient,YearBudget budget){
        yearlyClimateCoefficients.put(year, climateCoefficient);
        yearlyBudgets.put(year, budget);
    }

    /**
     * stores the record of one finished month. should be used once per month (300 times in a whole run).
     * precondition: record != null.
     * postcondition: record appended as last element, size +1; earlier records unchanged.
     */

    public void addMonth(MonthRecord record){
        monthlyRecords.add(record);
    }

    /*
        BAD(encapsulation/aliasing): the three getters return the internal collections directly
        so a client could add/remove/overwrite entries and break the history constraint and the invariant.
        Better: return unmodifiable views or copies. (Otherwise read-only; the object is not changed.)
    */

    public Map<Integer, Double> getYearlyClimateCoefficients() {
        return yearlyClimateCoefficients;
    }

    public Map<Integer, YearBudget> getYearlyBudgets() {
        return yearlyBudgets;
    }

    public List<MonthRecord> getMonthlyRecords() {
        return monthlyRecords;
    }

    public String getLocationName() {
        return locationName;
    }
}
