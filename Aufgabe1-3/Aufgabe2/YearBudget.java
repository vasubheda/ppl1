package Aufgabe2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part: Vishwas.
 * Year-end summary of how the yearly budget was used. All values are in budget units (not percent).
 * immediate (= short-term measures) + medium + longTerm = yearly budget.
 * longTermByMeasure tells how the long-term amount was split on the 5 long-term measures.
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
