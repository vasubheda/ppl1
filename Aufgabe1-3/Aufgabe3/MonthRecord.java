package Aufgabe3;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * NOTE: Part: Vishwas.
 * STYLE: object-oriented. An immutable value object, all fields private final.
 * GOOD (OO #8, weak coupling): the simulation hands the log one finished object per month. The mutable map is copied in the
 * constructor (new LinkedHashMap + unmodifiableMap), so the caller keeps no alias to the log's data and a later
 * change of the planner's Result cannot change the log. The alternative of storing the planner's Result directly
 * would couple the log to the planner.
 *
 * Reported after every month: short-term measures, medium-term measures, public acceptance.
 * invariant (precondition of the constructor): year in [1,100], month in [1,3], calamities >= 0,
 * shortTermTotal >= 0, shortTermDailyAverage = shortTermTotal / DAYS_PER_MONTH, mediumSpent >= 0,
 * paAfterUpdate and paAfterMeasures in [0,100], paAfterMeasures >= paAfterUpdate. The object does not change after the constructor.
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

    // postcondition: the public acceptance as the PA object calculated it from the monthly indicators (before the measures), in [0,100].
    public double getPaAfterUpdate() {
        return paAfterUpdate;
    }

    // postcondition: the public acceptance after the medium-term measures, the final value of the month, in [0,100], >= getPaAfterUpdate().
    public double getPaAfterMeasures() {
        return paAfterMeasures;
    }
}
