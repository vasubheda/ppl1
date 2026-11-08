package Aufgabe2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogInformation {

    // Yearly:
    private Map<Integer, Double> yearlyClimateCoefficients;
    private Map<Integer, YearBudget> yearlyBudgets;

    //monthly:
    private List<MonthRecord> monthlyRecords;

    public LogInformation() {
        yearlyClimateCoefficients = new HashMap<>();
        yearlyBudgets = new HashMap<>();
        monthlyRecords = new ArrayList<>();
    }

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
