package Aufgabe2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
* Part: Vishwas (simulation, budget and climate updation)
* This runs the 100 year simultion of 1 location.
* The location object contains weather, climate, PA(public acceptance), infrastructure and LogInformation.)
* A year has 3 months of 30 days.
* The flow :
* - every day : The weather creates the day, the simulation reads the 7 indicators, detects a calamity, if calamity happened, it pays the immediate measures from the immediate pool.
* - every month : The monthly indicator mean values are calculated, these means go to the PA object (honest PA + causes), then the simulation aims to improve the PA by taking medium-term measures (the bought infrastructure is added to the infra of the location). After all this, it logs month, short-ter measures, medium-term measures and PA (public acceptance)
* - every year : The remaining budget is split on the long-term measures, the climate coeff. A is updated (based on CO2 + long-term measures) and logged together with the long-term budget.
 */
public class BudgetSimulation {

    public static void simulation(Location location) {
        Climate climate = TeamAdapter.climate(location);
        BudgetCalculator budget = new BudgetCalculator(
                TeamAdapter.wealth(location), TeamAdapter.connectivity(location), climate.getA());
        CalamityDetector detector = new CalamityDetector();
        MediumTermPlanner mediumPlanner = new MediumTermPlanner();
        LongTermPlanner longTermPlanner = new LongTermPlanner();
        List<double[]> history = new ArrayList<>();    // the last 10 days that the weather is given

        // the calamities that can strike this location (own random, seeded by the name so runs are reproducible)
        Random rng = new Random(location.getName().hashCode());
        List<Calamity> calamityList = new ArrayList<>();
        calamityList.add(new Drought());
        calamityList.add(new Flood());
        calamityList.add(new FlashFreeze());

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
                    TeamAdapter.applyInfrastructure(location, climate.getA());      // built infrastructure calms the weather
                    TeamAdapter.advanceCalamities(location, calamityList, climate.getA(), rng);  // calamities may shock it
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

                // medium-term measures : the PA object calculates the honest PA, and the simulation aims to improves it
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

            // Long-term measures with what is left, then the yearly climate updation
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
