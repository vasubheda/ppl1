package Aufgabe2;

import java.util.List;
import java.util.Map;

public class LogInformation {

    // Yearly:
    private Map<Integer, Double> yearlyClimateCoefficients;
    private Map<Integer, YearBudget> yearlyBudgets;

    //monthly:
    private List<MonthRecord> monthlyRecords;

    public void addYear(int year, double climateCoefficient,YearBudget budget){
        yearlyClimateCoefficients.put(year, climateCoefficient);
        yearlyBudgets.put(year, budget);
    }

    public void addMonth(MonthRecord record){
        monthlyRecords.add(record);
    }

    public Map<Integer, Double> getYearlyClimateCoefficients() {
        return yearlyClimateCoefficients;
    }

    public Map<Integer, YearBudget> getYearlyBudgets() {
        return yearlyBudgets;
    }

    public List<MonthRecord> getMonthlyRecords() {
        return monthlyRecords;
    }
}
