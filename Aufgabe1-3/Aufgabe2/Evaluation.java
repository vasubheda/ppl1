package Aufgabe2;

import java.util.List;
import java.util.Map;

/**
 * Gets an Array of 88 Objects from the class LogInformation
 * uses the information to calculate points and the best M over all Simulations
 * It gives the logInformations and the coresponding points for the best M to the method printSummary
 * precondition: Input Array of logInfomations not null (simulation already ran to put input in)
 */

public class Evaluation {

    private static final double W_CLIMATE = 0.30;
    private static final double W_ACCEPTANCE = 0.30;
    private static final double W_CALAMITY = 0.20;
    private static final double W_BUDGET = 0.20;

    private static final double GOOD = 1.0;
    private static final double BAD = 3.0;

    private static final double ACCEPTABLE_CALAMITY_PER_MONTH = 1.0;

    public static final class Score {

        private final double climate;
        private final double acceptance;
        private final double calamity;
        private final double budget;
        private final double total; // 0..100

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

    public static double startEvaluation(LogInformation logInformation) {
        Score score = evaluate(logInformation);
        printSummary(logInformation, score);
        return score.getTotal();
    }

    private static Score evaluate(LogInformation log) {
        double climate    = scoreClimate(log.getYearlyClimateCoefficients());
        double acceptance = scoreAcceptance(log.getMonthlyRecords());
        double calamity   = scoreCalamity(log.getMonthlyRecords());
        double budget     = scoreBudget(log.getYearlyBudgets());
        return new Score(climate, acceptance, calamity, budget);
    }

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

    private static double scoreAcceptance(List<MonthRecord> months) {
        if (months.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (MonthRecord m : months) {
            sum += m.getPaAfterMeasures();
        }
        return SimConstants.clamp((sum / months.size()) / 100.0, 0.0, 1.0);
    }

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

    private static void printSummary(LogInformation log, Score s) {
        System.out.println("===== Evaluation der Simulation =====");
        System.out.printf("Teilnoten (0..1):%n");
        System.out.printf("  Klima       : %.2f%n", s.getClimate());
        System.out.printf("  Akzeptanz   : %.2f%n", s.getAcceptance());
        System.out.printf("  Katastrophen: %.2f%n", s.getCalamity());
        System.out.printf("  Budget      : %.2f%n", s.getBudget());
        System.out.printf("Gesamtnote    : %.1f / 100  (%s)%n", s.getTotal(), verdict(s.getTotal()));
        System.out.printf("Jahre: %d, Monate: %d%n",
                log.getYearlyClimateCoefficients().size(), log.getMonthlyRecords().size());
    }

    private static String verdict(double total) {
        if (total >= 75.0) return "sehr gut";
        if (total >= 55.0) return "ok";
        if (total >= 35.0) return "schwach";
        return "gescheitert";
    }


}