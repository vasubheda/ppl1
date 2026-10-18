import java.util.HashMap;
import java.util.Map;

public class LogInformation {

    private double climateCoefficient;
    private Map<Integer, Integer> shortMeasureOvershoot;
    private double[] avgShortMeasure;

    public LogInformation() {
        this.shortMeasureOvershoot = new HashMap<>();
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

    public double[] getAvgShortMeasure() {
        return avgShortMeasure;
    }

    public void setAvgShortMeasure(double[] avgShortMeasure) {
        this.avgShortMeasure = avgShortMeasure;
    }

    public void addAvgShortMeasure(double avgShortMeasure, int index) {
        this.avgShortMeasure[index] = avgShortMeasure;
    }
}
