package Aufgabe2;

/**
 * Part: Dhananjay Sharma
 *
 * Flash freeze: a sudden cold snap that damages crops and also can risk life of domestic animals. More likely when it is
 * already cool and dry.
 * F = a*CC + b*RG - c*Temp - d*Humidity
 * Effect of flash freezing: temperature down, humidity down
 */
public class FlashFreeze extends Calamity {
    private static final double A = 0.8;   // climate coefficient (more extreme weather with climate change)
    private static final double B = 0.25;  // random number
    private static final double C = 0.4;   // temperature
    private static final double D = 0.2;   // humidity
    private static final double THRESHOLD = 1.0;

    public String getName() {
        return "Flash freeze";
    }
    protected double score(Weather w, double cc, double rg) {
        return A * cc + B * rg - C * norm(w, SimConstants.TEMP) - D * norm(w, SimConstants.HUMIDITY);
    }
    protected double threshold() {
        return THRESHOLD;
    }
    protected void applyEffect(Weather w, double severity) {
        w.changeIndicator(SimConstants.TEMP, -shock(SimConstants.TEMP, severity));
        w.changeIndicator(SimConstants.HUMIDITY, -shock(SimConstants.HUMIDITY, severity));
    }
}