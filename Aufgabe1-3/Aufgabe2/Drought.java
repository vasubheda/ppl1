package Aufgabe2;

/**
 * Part: Dhananjay Sharma
 *
 * Drought: more likely when it is hot, the water reserve is low and the air is dry.
 * F = a*CC + b*RG + c*Temp - d*Water - e*Humidity
 * Effect: temperature up, water reserve down, humidity down.
 */
public class Drought extends Calamity {
    private static final double A = 0.5;   // climate coefficient
    private static final double B = 0.25;  // random number
    private static final double C = 1.0;   // temperature
    private static final double D = 0.3;   // water reserve
    private static final double E = 0.5;   // humidity
    private static final double THRESHOLD = 0.9;

    public String getName() {
        return "Drought";
    }
    protected double score(Weather w, double cc, double rg) {
        return A * cc + B * rg + C * norm(w, SimConstants.TEMP) - D * norm(w, SimConstants.WATER)
                - E * norm(w, SimConstants.HUMIDITY);
    }
    protected double threshold() {
        return THRESHOLD;
    }
    protected void applyEffect(Weather w, double severity) {
        w.changeIndicator(SimConstants.TEMP, shock(SimConstants.TEMP, severity));
        w.changeIndicator(SimConstants.WATER, -shock(SimConstants.WATER, severity));
        w.changeIndicator(SimConstants.HUMIDITY, -shock(SimConstants.HUMIDITY, severity));
    }}