package Aufgabe2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Part: Vishwas (simulation and budget, climate update).
 *
 * Runs the 100 year simulation of ONE location (the Location object carries Weather, Climate, PA, infrastructure
 * and the LogInformation). A year has 3 months of 30 days. Three decision rhythms:
 *  - every day:   the weather creates the day, the simulation reads the 7 indicators, detects a calamity spike and
 *                 pays the immediate (short-term) measures from the immediate pool
 *  - every month: the monthly indicator means go to the PA object (honest PA + causes), the simulation raises the PA
 *                 with medium-term measures (added to the infrastructure of the location) and logs month, short-term
 *                 measures, medium-term measures and PA
 *  - every year:  the remaining budget is split on the long-term measures, the climate coefficient A is updated
 *                 (CO2 + long-term measures) and logged together with the long-term budget
 *
 * Modularization unit: module (static method, all state lives in the objects created per run).
 * Not responsible for: weather values, calamity formulas and the PA calculation (teammates, see TeamAdapter).
 */
public class BudgetSimulation {

    /**
     * Precondition: location is complete (weather, climate, PA and log information are set).
     * Postcondition: the log information of the location holds 300 monthly and 100 yearly records.
     */
    public static void simulation(Location location) {
        Climate climate = TeamAdapter.climate(location);
        BudgetCalculator budget = new BudgetCalculator(
                TeamAdapter.wealth(location), TeamAdapter.connectivity(location), climate.getA());
        CalamityDetector detector = new CalamityDetector();
        MediumTermPlanner mediumPlanner = new MediumTermPlanner();
        LongTermPlanner longTermPlanner = new LongTermPlanner();
        List<double[]> history = new ArrayList<>();     // the last 0..10 days that the weather gets

        for (int year = 1; year <= SimConstants.YEARS; year++) {
            budget.startYear();
            double[] yearSum = new double[SimConstants.INDICATOR_COUNT];

            for (int month = 1; month <= SimConstants.MONTHS_PER_YEAR; month++) {
                budget.startMonth();
                double[] monthSum = new double[SimConstants.INDICATOR_COUNT];
                double shortTermTotal = 0.0;
                int calamities = 0;

                for (int day = 1; day <= SimConstants.DAYS_PER_MONTH; day++) {
                    TeamAdapter.advanceWeather(location, history, climate.getA());
                    double[] today = TeamAdapter.readIndicators(location);

                    CalamityDetector.Event event = detector.check(today);
                    if (event != null) {
                        calamities++;
                        shortTermTotal += budget.spendImmediate(budget.calamityCost(event));
                    }

                    for (int i = 0; i < today.length; i++) {
                        monthSum[i] += today[i];
                        yearSum[i] += today[i];
                    }
                    history.add(today);
                    if (history.size() > SimConstants.HISTORY_DAYS) {
                        history.remove(0);
                    }
                }

                // medium-term measures: the PA object calculates the honest PA, the simulation improves it
                double[] monthMeans = mean(monthSum, SimConstants.DAYS_PER_MONTH);
                TeamAdapter.PaReport report = TeamAdapter.updatePublicAcceptance(location, monthMeans);
                MediumTermPlanner.Result medium = mediumPlanner.act(
                        report.getDelta(), report.getValue(), report.getCauses(), budget);
                if (medium.getGain() > 0.0) {
                    TeamAdapter.increasePublicAcceptance(location, medium.getGain());
                }
                for (Map.Entry<String, Integer> bought : medium.getPurchases().entrySet()) {
                    TeamAdapter.addInfrastructure(location, bought.getKey(),
                            MediumTermPlanner.indicatorsOf(bought.getKey()), bought.getValue());
                }
                TeamAdapter.logMonth(location, new MonthRecord(year, month,
                        shortTermTotal, shortTermTotal / SimConstants.DAYS_PER_MONTH, calamities,
                        medium.getSpent(), medium.getPurchases(),
                        report.getValue(), TeamAdapter.publicAcceptanceValue(location)));
            }

            // long-term measures with what is left, then the yearly climate update
            double[] yearMeans = mean(yearSum, SimConstants.MONTHS_PER_YEAR * SimConstants.DAYS_PER_MONTH);
            Map<String, Double> longTermAllocation = longTermPlanner.allocate(
                    budget.remainder(), yearMeans, detector, TeamAdapter.publicAcceptanceValue(location));
            int[] newLevels = longTermPlanner.newWholeLevels();
            for (int i = 0; i < newLevels.length; i++) {
                TeamAdapter.addInfrastructure(location, SimConstants.LONG_NAMES[i],
                        new int[]{SimConstants.LONG_LINKED_INDICATOR[i]}, newLevels[i]);
            }
            detector.endYear();
            climate.setA(ClimateUpdater.nextA(climate.getA(), yearMeans[SimConstants.CO2], longTermPlanner.mitigation()));
            TeamAdapter.logYear(location, year, climate.getA(), budget.closeYear(longTermAllocation));
        }
    }

    private static double[] mean(double[] sum, int days) {
        double[] result = new double[sum.length];
        for (int i = 0; i < sum.length; i++) {
            result[i] = sum[i] / days;
        }
        return result;
    }
}
