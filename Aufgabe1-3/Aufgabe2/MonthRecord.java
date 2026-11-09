package Aufgabe2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
* Part : Vishwas.
* This is what the simulation reports to LogInformation after every month -
* short-term measures, medium-term measures and public acceptance.
*
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

    // money paid for short-term measures this month
    public double getShortTermTotal() {
        return shortTermTotal;
    }

    // the short-term measures daily average of the month
    public double getShortTermDailyAverage() {
        return shortTermDailyAverage;
    }

    // the number of calamities that happened this month
    public int getCalamities() {
        return calamities;
    }

    public double getMediumSpent() {
        return mediumSpent;
    }

    // the medium-term measures purchases : name -> how often
    public Map<String, Integer> getMediumPurchases() {
        return mediumPurchases;
    }

    /** the "honest" public acceptance, as calculated by the PA object from the monthly indicators */
    // the honest public acceptance, as calculated by the PA object from the monthly indicators.
    public double getPaAfterUpdate() {
        return paAfterUpdate;
    }

    // public acceptance that was raised after the medium-term measures (this value is the final one of the month)
    public double getPaAfterMeasures() {
        return paAfterMeasures;
    }
}
