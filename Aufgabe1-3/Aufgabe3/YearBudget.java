package Aufgabe3;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NOTE: Part: Vishwas.
 * STYLE: object-oriented. Immutable value object (final fields, longTermByMeasure copied and unmodifiable).
 * GOOD (OO #9): same idea as MonthRecord, the log keeps its own copy, so BudgetCalculator and LongTermPlanner
 * cannot change a year that was already logged.
 *
 * Year-end summary of how the yearly budget was used (budget units, not percent).
 * invariant: immediate, medium, longTerm >= 0, immediate + medium + longTerm = yearly budget (= getTotal()),
 * sum of the values of longTermByMeasure = longTerm (up to rounding). The object does not change after the constructor.
 */
public final class YearBudget {
    private final double immediate;
    private final double medium;
    private final double longTerm;
    private final Map<String, Double> longTermByMeasure;

    public YearBudget(double immediate, double medium, double longTerm, Map<String, Double> longTermByMeasure) {
        this.immediate = immediate;
        this.medium = medium;
        this.longTerm = longTerm;
        this.longTermByMeasure = Collections.unmodifiableMap(new LinkedHashMap<>(longTermByMeasure));
    }

    public double getImmediate() {
        return immediate;
    }

    public double getMedium() {
        return medium;
    }

    public double getLongTerm() {
        return longTerm;
    }

    public Map<String, Double> getLongTermByMeasure() {
        return longTermByMeasure;
    }

    public double getTotal() {
        return immediate + medium + longTerm;
    }
}
