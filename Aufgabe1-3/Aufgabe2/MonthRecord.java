package Aufgabe2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part: Vishwas.
 * What the simulation reports to LogInformation after every month (diagram output 1):
 * short-term measures, medium-term measures and public acceptance.
 */
public final class MonthRecord {
    private final int year;
    private final int month;
    private final double shortTermTotal;
    private final double shortTermDailyAverage;
    private final int calamities;
    private final double mediumSpent;
    private final Map<String, Integer> mediumPurchases;
    private final double paAfterUpdate;
    private final double paAfterMeasures;

    public MonthRecord(int year, int month, double shortTermTotal, double shortTermDailyAverage, int calamities,
                       double mediumSpent, Map<String, Integer> mediumPurchases,
                       double paAfterUpdate, double paAfterMeasures) {
        this.year = year;
        this.month = month;
        this.shortTermTotal = shortTermTotal;
        this.shortTermDailyAverage = shortTermDailyAverage;
        this.calamities = calamities;
        this.mediumSpent = mediumSpent;
        this.mediumPurchases = Collections.unmodifiableMap(new LinkedHashMap<>(mediumPurchases));
        this.paAfterUpdate = paAfterUpdate;
        this.paAfterMeasures = paAfterMeasures;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    /** money paid for short-term (immediate) measures in this month */
    public double getShortTermTotal() {
        return shortTermTotal;
    }

    /** average per day of the month for short-term measures */
    public double getShortTermDailyAverage() {
        return shortTermDailyAverage;
    }

    /** number of calamities the simulation detected in this month */
    public int getCalamities() {
        return calamities;
    }

    public double getMediumSpent() {
        return mediumSpent;
    }

    /** bought medium-term measures: name -> how often */
    public Map<String, Integer> getMediumPurchases() {
        return mediumPurchases;
    }

    /** the "honest" public acceptance, as calculated by the PA object from the monthly indicators */
    public double getPaAfterUpdate() {
        return paAfterUpdate;
    }

    /** public acceptance after the medium-term measures raised it (this value is the final one of the month) */
    public double getPaAfterMeasures() {
        return paAfterMeasures;
    }
}
