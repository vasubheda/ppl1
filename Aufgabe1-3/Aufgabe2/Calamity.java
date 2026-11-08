package Aufgabe2;

import java.util.Random;

/**
 * Part: Dhananjay Sharma
 * AI use: Claude chat (conversation mode) was used to translate the exercise and the script to English and to
 * discuss the design. AI usage in dailyCheck.
 *
 * A calamity is a rare natural event (drought, flood, flash freeze) that suddenly changes the weather of a
 * location. Every day it is checked if the calamity happens. If it happens, it changes some indicators of the
 * weather at once. After that the weather brings itself back to normal.
 *
 * Trigger: every calamity has its own formula made of the climate coefficient (CC),
 * one random number (RG) and some indicators, for example for a drought
 *
 * The indicators are used normalised to 0..1 (SimConstants.normalise), so coefficients are comparable.
 * If F is over the threshold of the calamity, it happens.
 *
 * severity is not fixed. The further F is over the threshold, the stronger the
 * calamity (continuous between SEVERITY_LOW and SimConstants.SEVERITY_MAX). The shock of one indicator is
 * NOMINAL_SHOCK * severity * range of the indicator, the rule described at SimConstants.NOMINAL_SHOCK, so the
 * simulation can estimate the severity from the size of the spike.
 *
 * Adjusting: the coefficients a, b, c,... and the threshold of every subclass are the parameters of the model.
 *
 * After a calamity it stays active for DURATION_DAYS days. The same calamity cannot start again for
 * COOLDOWN_DAYS days, because the weather needs time to come back to normal. Without this pause the changed
 * weather (e.g. hot and dry after a drought) would start the same calamity again and again.
 *
 * Calamity is an abstract class, Drought, Flood and FlashFreeze are its subtypes.
 * The daily check is the same for all (dailyCheck), only the formula (score) and the effect (applyEffect) are
 * different and are overridden in the subclasses. Which score() and applyEffect() run is decided at run time
 * (dynamic binding), so new calamities can be added as new subclasses without changing the rest.
 */
public abstract class Calamity {
    // days a calamity stays active after it started
    protected static final int DURATION_DAYS = 7;
    // days after the start in which the same calamity cannot start again (one month)
    protected static final int COOLDOWN_DAYS = 30;

    // lowest severity. A bit above SimConstants.SEVERITY_MIN (0.5) so that even the weakest shock is clearly
    // bigger than the spike threshold of the simulation
    private static final double SEVERITY_LOW = 0.6;
    // how fast the severity grows with the part of F that is over the threshold
    private static final double SEVERITY_SLOPE = 4.0;
    private int daysSinceStart = COOLDOWN_DAYS;   // "long ago" at the beginning
    private double severity = 0.0;

    /**
     * Daily check, called once per day after the weather of the day was created.
     * In the cooldown time after the last start nothing happens. Otherwise the formula is calculated and if it
     * is over the threshold, the calamity starts and changes the weather. Returns true only on the day it starts.
     */
    public final boolean dailyCheck(Weather weather, double climateCoefficient, Random random) {
        daysSinceStart++;
        if (daysSinceStart < COOLDOWN_DAYS) {
            return false;
        }
        double cc = climateCoefficient - SimConstants.A_MIN;   // 0 = no climate change
        double f = score(weather, cc, random.nextGaussian());
        if (f <= threshold()) {
            return false;
        }
        severity = severityFor(f - threshold());
        applyEffect(weather, severity);
        daysSinceStart = 0;
        return true;
    }

    /** true while the calamity is active, started in the last DURATION_DAYS days */
    public boolean isActive() {
        return daysSinceStart < DURATION_DAYS;
    }
    /** strength of the last calamity, between SEVERITY_LOW and SimConstants.SEVERITY_MAX */
    public double getSeverity() {
        return severity;
    }
    /** name of the calamity e.g. Drought */
    public abstract String getName();
    /** value of the trigger formula F for today; cc = climate coefficient above A_MIN, rg = random number N(0,1) */
    protected abstract double score(Weather weather, double cc, double rg);

    /** F has to be over this value for the calamity to happen */
    protected abstract double threshold();

    /** changes the indicators of the weather with the given severity */
    protected abstract void applyEffect(Weather weather, double severity);

    // Helpers for the subclasses

    /** indicator of the weather normalised to 0..1 (0 = lowest possible, 1 = highest possible value) */
    protected static double norm(Weather weather, int index) {
        return SimConstants.normalise(index, weather.getValue(index));
    }

    /**
     * size of the shock of one indicator for this severity
     *
     * Static procedure that only calculates a number from its parameters and the global
     * constants of SimConstants, no object state is used.
     */
    protected static double shock(int index, double severity) {
        return SimConstants.NOMINAL_SHOCK * severity * SimConstants.range(index);
    }

    /** severity grows continuously with how far F is over the threshold */
    private static double severityFor(double overThreshold) {
        return SimConstants.clamp(SEVERITY_LOW + SEVERITY_SLOPE * overThreshold, SEVERITY_LOW, SimConstants.SEVERITY_MAX);
    }
}