package Aufgabe3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Part: Vishwas (functional part of assignment 3)
 *
 * STYLE: functional (the print procedure at the bottom is the only procedural part, see there).
 * Evaluates a finished simulation run of one location from its LogInformation, in two parts:
 *  A) stream analysis of the monthly records and yearly budgets (sums, rankings, moving average),
 *  B) a recursive what-if projection of the climate coefficient A with different mitigation levels.
 *
 * How it fits with the rest of the program (referential transparency):
 *  - It runs only AFTER the simulation, from Test. It never changes Location, LogInformation or any simulation object.
 *  - All functions are static, read only their parameters (and the constants of SimConstants), have no state,
 *    no random numbers and no output. Same arguments always give the same result.
 *  - MonthRecord and YearBudget are immutable. The collections from LogInformation are mutable, so the entry
 *    point print() copies them first and gives the pure functions only the copies.
 *  - Only print() writes to System.out. It is the single place with a side effect.
 *
 */
public final class FunctionalAnalysis {

    private FunctionalAnalysis() {
    }

    // ---------------------------------------------------------------- Part A: streams

    /**
     * Higher-order function: groups the items by key and sums the value of each item. Result is sorted by key.
     * STYLE: functional. pure, the two function parameters decide what is grouped and summed.
     * precondition: items, keyOf and valueOf are not null; keyOf never returns null.
     * postcondition: the result is a new map, the sum of its values equals the sum of valueOf over all items.
     */
    static <T, K extends Comparable<K>> Map<K, Double> sumBy(List<T> items, Function<T, K> keyOf,
                                                            ToDoubleFunction<T> valueOf) {
        return items.stream().collect(Collectors.groupingBy(keyOf, TreeMap::new, Collectors.summingDouble(valueOf)));
    }

    /** calamities per year, e.g. {1=3.0, 2=1.0, ...} */
    static Map<Integer, Double> calamitiesPerYear(List<MonthRecord> months) {
        return sumBy(months, MonthRecord::getYear, MonthRecord::getCalamities);
    }

    /** money paid for short-term measures per year */
    static Map<Integer, Double> shortTermPerYear(List<MonthRecord> months) {
        return sumBy(months, MonthRecord::getYear, MonthRecord::getShortTermTotal);
    }

    /**
     * the n years with the highest value, worst first.
     * postcondition: at most n entries, sorted by descending value.
     */
    static List<Map.Entry<Integer, Double>> worstYears(Map<Integer, Double> perYear, int n) {
        return perYear.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /** how often each medium-term measure was bought over the whole run (measure name -> count) */
    static Map<String, Integer> purchaseTotals(List<MonthRecord> months) {
        return months.stream()
                .flatMap(m -> m.getMediumPurchases().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum, TreeMap::new));
    }

    /**
     * share (0..1) of the whole budget that went to immediate, medium and long-term measures.
     * precondition: budgets is not empty and its total is greater than 0.
     * postcondition: array of 3 values {immediate, medium, longTerm} that add up to 1.
     */
    static double[] budgetShares(List<YearBudget> budgets) {
        double total = budgets.stream().mapToDouble(YearBudget::getTotal).sum();
        return new double[]{
                share(budgets, YearBudget::getImmediate, total),
                share(budgets, YearBudget::getMedium, total),
                share(budgets, YearBudget::getLongTerm, total)};
    }

    private static double share(List<YearBudget> budgets, ToDoubleFunction<YearBudget> part, double total) {
        return budgets.stream().mapToDouble(part).sum() / total;
    }

    /**
     * moving average over a window of the given values.
     * precondition: window is at least 1.
     * postcondition: result has values.size() - window + 1 entries (empty if values is shorter than window).
     */
    static List<Double> movingAverage(List<Double> values, int window) {
        return IntStream.rangeClosed(0, values.size() - window)
                .mapToObj(i -> values.subList(i, i + window).stream().mapToDouble(Double::doubleValue).average().orElse(0.0))
                .collect(Collectors.toList());
    }

    /** calamities per decade (decade 0 = years 1-10, decade 1 = years 11-20, ...) */
    static Map<Integer, Double> calamitiesPerDecade(List<MonthRecord> months) {
        return sumBy(months, m -> (m.getYear() - 1) / 10, MonthRecord::getCalamities);
    }

    /**
     * Higher-order function: mean of a number taken from every item.
     * postcondition: 0.0 for an empty list, otherwise the arithmetic mean.
     */
    static <T> double meanOf(List<T> items, ToDoubleFunction<T> valueOf) {
        return items.stream().mapToDouble(valueOf).average().orElse(0.0);
    }

    /**
     * the months (by year) in which public acceptance was below the limit, as "year" -> number of such months.
     * postcondition: only years with at least one such month are in the result.
     */
    static Map<Integer, Long> lowAcceptanceMonths(List<MonthRecord> months, double limit) {
        return months.stream()
                .filter(m -> m.getPaAfterMeasures() < limit)
                .collect(Collectors.groupingBy(MonthRecord::getYear, TreeMap::new, Collectors.counting()));
    }

    // ---------------------------------------------------------------- Part B: recursion

    /**
     * Recursive projection of the climate coefficient: applies the step function once per year.
     * STYLE: functional. recursion instead of a loop, step is a function parameter (higher-order).
     * precondition: years is not negative, step is not null.
     * postcondition: result = step applied years times to a, for years = 0 it is a itself.
     */
    static double projectA(double a, int years, DoubleUnaryOperator step) {
        return years == 0 ? a : projectA(step.applyAsDouble(a), years - 1, step);
    }

    /**
     * Recursive search: the first year (counted from year) in which A is above the limit, empty if it never is
     * within the remaining years.
     * STYLE: functional. recursion with the Optional type instead of a loop with a break.
     * precondition: remaining is not negative, step is not null.
     */
    static Optional<Integer> firstYearAbove(double a, int year, int remaining, double limit, DoubleUnaryOperator step) {
        if (a > limit) {
            return Optional.of(year);
        }
        return remaining == 0 ? Optional.empty()
                : firstYearAbove(step.applyAsDouble(a), year + 1, remaining - 1, limit, step);
    }

    /** one yearly step of A with a fixed reference CO2 level and the given mitigation (uses the pure ClimateUpdater) */
    static DoubleUnaryOperator stepWith(double mitigation) {
        return a -> ClimateUpdater.nextA(a, SimConstants.CO2_REF, mitigation);
    }

    // ---------------------------------------------------------------- procedural boundary

    /**
     * STYLE: procedural (boundary between the simulation and the functional part).
     * Takes the log of one finished run, copies its data, calls the pure functions and prints the results.
     * precondition: the simulation of this log is finished (300 monthly records, 100 yearly budgets).
     */
    public static void print(LogInformation log) {
        List<MonthRecord> months = new ArrayList<>(log.getMonthlyRecords());
        List<YearBudget> budgets = new ArrayList<>(new TreeMap<>(log.getYearlyBudgets()).values());
        Map<Integer, Double> climate = new TreeMap<>(log.getYearlyClimateCoefficients());

        System.out.println("--- Functional analysis: " + log.getLocationName() + " ---");
        System.out.println("Calamities in total: " + calamitiesPerYear(months).values().stream().mapToDouble(d -> d).sum());
        System.out.println("3 years with most short-term spending: " + worstYears(shortTermPerYear(months), 3));
        System.out.println("Medium-term purchases: " + purchaseTotals(months));

        double[] s = budgetShares(budgets);
        System.out.printf("Budget shares: immediate %.1f%%, medium %.1f%%, long-term %.1f%%%n", s[0] * 100, s[1] * 100, s[2] * 100);

        List<Double> pa = months.stream().map(MonthRecord::getPaAfterMeasures).collect(Collectors.toList());
        List<Double> avg = movingAverage(pa, 12);
        System.out.printf("Public acceptance, 12-month average: first %.1f, last %.1f%n", avg.get(0), avg.get(avg.size() - 1));

        System.out.println("Calamities per decade: " + calamitiesPerDecade(months));
        System.out.printf("Mean short-term cost per month: %.2f, years with a month of public acceptance below 20: %s%n",
                meanOf(months, MonthRecord::getShortTermTotal), lowAcceptanceMonths(months, 20.0).size());

        double start = climate.get(1);        // A at the end of year 1 is the start of the what-if
        int years = SimConstants.YEARS - 1;
        System.out.printf("What-if A in year %d (start %.2f): no measures %.2f, weak %.2f, strong %.2f, real run %.2f%n",
                SimConstants.YEARS, start,
                projectA(start, years, stepWith(0.0)),
                projectA(start, years, stepWith(0.003)),
                projectA(start, years, stepWith(0.006)),
                climate.get(SimConstants.YEARS));
        System.out.println("First year with A above 2.0 without measures: "
                + firstYearAbove(start, 1, years, 2.0, stepWith(0.0)).map(String::valueOf).orElse("never"));
    }
}
