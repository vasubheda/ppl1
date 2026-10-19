import java.util.HashMap;
import java.util.Map;

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

    public void addShortMeasureOvershoot(int year, int day) {
        this.shortMeasureOvershoot.put(year, day);
    }

    public Map<Integer, Double> getShortfallAmount() {
        return shortfallAmount;
    }

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
