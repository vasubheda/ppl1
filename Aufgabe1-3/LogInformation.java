import java.util.HashMap;
import java.util.Map;

/**
 * Class to record results of simulation
 * it has to record following things:
 * the climateCoefficient after 100years of simulation
 * If the Budget overshoots so remainingBudget<0 it has to record what year, day and how much
 * The avarage spending for short-term measures each 10 years total of 100 so in whole 10
 * precondition: the logInfomation has to be initalized with the sterting values of A and M so they can be identified and used in the {@link Test#evaluation(LogInformation[])}
 */

 /*
    Modulisationunit of the class:
    Object: all class variables are object because there are 88 different logs fromm 88 different simulations
    Abstraktion: This class is responsible for storing the recorded results of the simulation to be used in the evaluation
 */



public class LogInformation {

    private final double startClimateCoefficient;
    private final int startMiddleMeasureBudgetPart;
    private double climateCoefficient;
    private Map<Integer, Integer> shortMeasureOvershoot;
    private Map<Integer, Double> shortfallAmount;
    private double[] avgShortMeasure;

    public LogInformation(double startClimateCoefficient, int startMiddleMeasureBudgetPart) {
        this.startClimateCoefficient = startClimateCoefficient;
        this.startMiddleMeasureBudgetPart = startMiddleMeasureBudgetPart;
        this.shortMeasureOvershoot = new HashMap<>();
        this.shortfallAmount = new HashMap<>();
        this.avgShortMeasure = new double[10];
    }

    public double getClimateCoefficient() {
        return climateCoefficient;
    }

    public void setClimateCoefficient(double climateCoefficient) {
        this.climateCoefficient = climateCoefficient;
    }

    public Map<Integer, Integer> getShortMeasureOvershoot() {
        return shortMeasureOvershoot;
    }

    /**
     * this method is for logging when (what year and on what day did this happen) the budget is overshot from the short-term costs
     * the method should only be used once a year on the day it happens
     * Possible values: year:1-100, day:1-90
     */

    public void addShortMeasureOvershoot(int year, int day) {
        this.shortMeasureOvershoot.put(year, day);
    }



    public Map<Integer, Double> getShortfallAmount() {
        return shortfallAmount;
    }

    /**
     * this method stores for what amount that year was overshot
     */

    public void addShortfallAmount(int year, double amount) {
        this.shortfallAmount.put(year, amount);
    }

    public double[] getAvgShortMeasure() {
        return avgShortMeasure;
    }

    public void setAvgShortMeasure(double[] avgShortMeasure) {
        this.avgShortMeasure = avgShortMeasure;
    }

    public void addAvgShortMeasure(double avgShortMeasure, int index) {
        this.avgShortMeasure[index] = avgShortMeasure;
    }

    public double getStartClimateCoefficient() {
        return startClimateCoefficient;
    }

    public int getStartMiddleMeasureBudgetPart() {
        return startMiddleMeasureBudgetPart;
    }
}
