package Aufgabe2;

import java.util.List;
import java.util.Map;

/*
    Modulisationunit of the class:
    Modul: the class is a modul because it is not used to create objects, it only works on the log it gets
*/
/**
 * Abstraktion: This class is responsible for judging how well ONE simulation of ONE location went. It reads
 * the recorded LogInformation, turns it into four partial scores (climate, acceptance, calamities, budget)
 * and combines them with fixed weights into one grade from 0 to 100, which it prints.
 * All weights and thresholds are constants at the top, so the scoring can be adjusted in one place.
*/


public class Evaluation {

    private static final double W_CLIMATE = 0.30;
    private static final double W_ACCEPTANCE = 0.30;
    private static final double W_CALAMITY = 0.20;
    private static final double W_BUDGET = 0.20;

    private static final double GOOD = 1.0;
    private static final double BAD = 2.2;

    private static final double ACCEPTABLE_CALAMITY_PER_MONTH = 1.0;

    public static final class Score {

        private final double climate;
        private final double acceptance;
        private final double calamity;
        private final double budget;
        private final double total; // 0..100

        /*
            STYLE: object-oriented. Score is a small object that bundles its own data and hides it.
            the fields are private and final set once in the constructor and only readable through getters.
            That the data and the methods that give it out sit together in one encapsulated object is what makes this object-oriented.
        */

        /**
         * holds the four partial scores (each 0..1) and the total grade (0..100) of one run.
         * The values are set once in the constructor and can only be read with getters.
         */

        private Score(double climate, double acceptance, double calamity, double budget) {
            this.climate = climate;
            this.acceptance = acceptance;
            this.calamity = calamity;
            this.budget = budget;
            this.total = 100.0 * (W_CLIMATE * climate + W_ACCEPTANCE * acceptance
                    + W_CALAMITY * calamity + W_BUDGET * budget);
        }

        public double getClimate() {
            return climate;
        }

        public double getAcceptance() {
            return acceptance;
        }

        public double getCalamity() {
            return calamity;
        }

        public double getBudget() {
            return budget;
        }

        public double getTotal() {
            return total;
        }
    }

    /**
     * entry point: calculates the score of the location from its logInformation and prints the summary.
     * Returns the total grade (0..100) too, in case a caller wants to compare several locations.
     * precondition: logInformation != null and already filled (the simulation ran, at least one month and
     * one year were logged).
     */

    public static double startEvaluation(LogInformation logInformation) {
        Score score = evaluate(logInformation);
        printSummary(logInformation, score);
        return score.getTotal();
    }

    /*
        STYLE: boundary object-oriented -> procedural. Here the two paradigms meet.
        The OO log objects are asked for their data through getters (getYearlyClimateCoefficients, getMonthlyRecords, ...)
        that data is handed as plain numbers to the procedural score functions below
        and the plain results are wrapped back into the OO Score object that is returned.
    */

    /**
     * builds the four partial scores from the log data and combines them into the Score object
     */

    private static Score evaluate(LogInformation log) {
        double climate    = scoreClimate(log.getYearlyClimateCoefficients());
        double acceptance = scoreAcceptance(log.getMonthlyRecords());
        double calamity   = scoreCalamity(log.getMonthlyRecords());
        double budget     = scoreBudget(log.getYearlyBudgets());
        return new Score(climate, acceptance, calamity, budget);
    }

    /*
        STYLE: procedural. The four score methods are static pure functions.
        they only read their parameters, create no object and keep no state. The same input always gives the same number.
    */

    /**
     * climate score: the last climate coefficient A mapped from [GOOD..BAD] to [1..0] (low A is better)
     */

    private static double scoreClimate(Map<Integer, Double> yearlyA) {
        if (yearlyA.isEmpty()) {
            return 0.0;
        }
        int lastYear = Integer.MIN_VALUE;
        for (int year : yearlyA.keySet()) {
            lastYear = Math.max(lastYear, year);
        }
        double finalA = yearlyA.get(lastYear);
        return SimConstants.clamp((BAD - finalA) / (BAD - GOOD), 0.0, 1.0);
    }

    /**
     * acceptance score: average public acceptance (after the measures) over all months, divided by 100
     */

    private static double scoreAcceptance(List<MonthRecord> months) {
        if (months.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (MonthRecord m : months) {
            sum += m.getPaAfterMeasures();
        }
        return SimConstants.clamp(((sum / months.size()) - 40.0) / 50.0, 0.0, 1.0);
    }

    /**
     * calamity score: 1 - (average calamities per month / ACCEPTABLE_CALAMITY_PER_MONTH), fewer is better
     */

    private static double scoreCalamity(List<MonthRecord> months) {
        if (months.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (MonthRecord m : months) {
            total += m.getCalamities();
        }
        double avgPerMonth = total / months.size();
        return SimConstants.clamp(1.0 - avgPerMonth / ACCEPTABLE_CALAMITY_PER_MONTH, 0.0, 1.0);
    }

    /**
     * budget score: share of the foresighted budget (medium + long term) in the whole budget over all years
     */

    private static double scoreBudget(Map<Integer, YearBudget> yearlyBudgets) {
        if (yearlyBudgets.isEmpty()) {
            return 0.0;
        }
        double proactive = 0.0;
        double total = 0.0;
        for (YearBudget b : yearlyBudgets.values()) {
            proactive += b.getMedium() + b.getLongTerm();
            total += b.getTotal();
        }
        if (total <= 0.0) {
            return 0.0;
        }
        return SimConstants.clamp(proactive / total, 0.0, 1.0);
    }

    /**
     * prints a readable summary of the score (only console output, no calculation any more)
     */

    private static void printSummary(LogInformation log, Score s) {
        System.out.printf("\n===== Evaluation der Simulation von %s =====\n", log.getLocationName());
        System.out.printf("Teilnoten (0..1):\n");
        System.out.printf("  Klima       : %.2f\n", s.getClimate());
        System.out.printf("  Akzeptanz   : %.2f\n", s.getAcceptance());
        System.out.printf("  Katastrophen: %.2f\n", s.getCalamity());
        System.out.printf("  Budget      : %.2f\n", s.getBudget());
        System.out.printf("Gesamtnote    : %.1f / 100  (%s)\n", s.getTotal(), verdict(s.getTotal()));
        System.out.printf("Jahre: %d, Monate: %d\n",
                log.getYearlyClimateCoefficients().size(), log.getMonthlyRecords().size());
    }

    /*
        STYLE: procedural. A pure function made only of if and return on its parameter no object and no state is involved.
    */

    /**
     * small helper: turns the total grade into a word verdict
     */

    private static String verdict(double total) {
        if (total >= 85.0) return "sehr gut";
        if (total >= 68.0) return "ok";
        if (total >= 45.0) return "schwach";
        return "gescheitert";
    }


}