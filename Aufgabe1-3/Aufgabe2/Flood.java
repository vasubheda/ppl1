package Aufgabe2;

/**
 * Part: Dhananjay Sharma
 *
 * Flood: more likely when the water reserve is high and the air is humid.
 * F = a*CC + b*RG + c*Water + d*Humidity
 * Effect: water reserve up, humidity up.
 */
public class Flood extends Calamity {
    private static final double A = 0.8;   // climate coefficient (more heavy rain with climate change)
    private static final double B = 0.25;  // random number
    private static final double C = 0.4;   // water reserve
    private static final double D = 0.5;   // humidity
    private static final double THRESHOLD = 1.1;


    public String getName() {
        return "Flood";
    }
    protected double score(Weather w, double cc, double rg) {
        return A * cc + B * rg + C * norm(w, SimConstants.WATER) + D * norm(w, SimConstants.HUMIDITY);
    }
    protected double threshold() {
        return THRESHOLD;
    }
    protected void applyEffect(Weather w, double severity) {
        w.changeIndicator(SimConstants.WATER, shock(SimConstants.WATER, severity));
        w.changeIndicator(SimConstants.HUMIDITY, shock(SimConstants.HUMIDITY, severity));
    }
}
