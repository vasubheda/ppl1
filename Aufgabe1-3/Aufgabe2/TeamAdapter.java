package Aufgabe2;

import Aufgabe1.Climate;
import Aufgabe1.Weather;
import Aufgabe1.YearBudget;

import java.util.ArrayList;
import java.util.List;

/**
 * Part: Vishwas.
 *
 * THE ONLY PLACE where the simulation code touches classes of the teammates (Aufgabe1.Location, Aufgabe1.Weather,
 * PublicAcceptance, Aufgabe1.LogInformation). The names of their methods are still generic (see stubs/), when their real
 * code is ready, only this file has to be adapted. The rest of the simulation works with plain values:
 * double[7] for the indicators (index constants in SimConstants), List<Integer> for the causes of a PA drop.
 *
 * Modularization unit: module (static methods only, no state).
 */
public final class TeamAdapter {
    private TeamAdapter() {
    }

    /** What the PA object returned after the monthly update, translated into simulation terms. */
    public static final class PaReport {
        private final double value;
        private final double delta;
        private final List<Integer> causes;

        public PaReport(double value, double delta, List<Integer> causes) {
            this.value = value;
            this.delta = delta;
            this.causes = causes;
        }

        /** the honest public acceptance */
        public double getValue() {
            return value;
        }

        /** change of PA caused by the indicators (negative = got lower) */
        public double getDelta() {
            return delta;
        }

        /** indicator indexes (SimConstants.TEMP ...) whose threshold was violated */
        public List<Integer> getCauses() {
            return causes;
        }
    }

    // ------------------------------------------------------------ Aufgabe1.Location
    public static int wealth(Location location) {
        return location.getWealth();
    }

    public static int connectivity(Location location) {
        return location.getConnectivity();
    }

    public static Climate climate(Location location) {
        return location.getClimate();
    }

    // ------------------------------------------------------------- Aufgabe1.Weather
    /**
     * Lets the weather of the location create the next day. The simulation gives the last 0..10 days
     * (oldest first, each as double[7]) and the climate coefficient.
     */
    public static void advanceWeather(Location location, List<double[]> last10Days, double climateCoefficient) {
        location.getWeather().nextDay(last10Days, climateCoefficient);
    }

    /** reads the 7 indicator values of the current day with the 7 getters of the weather */
    public static double[] readIndicators(Location location) {
        Weather w = location.getWeather();
        double[] values = new double[SimConstants.INDICATOR_COUNT];
        values[SimConstants.TEMP] = w.getTemp();
        values[SimConstants.WATER] = w.getWater();
        values[SimConstants.CO2] = w.getCo2();
        values[SimConstants.HUMIDITY] = w.getHumidity();
        values[SimConstants.FINE_DUST] = w.getFineDust();
        values[SimConstants.CLOUDINESS] = w.getCloudiness();
        values[SimConstants.WINDSPEED] = w.getWindspeed();
        return values;
    }

    // ------------------------------------------------- Public acceptance
    /** gives the monthly indicator means to the PA object, which calculates the honest PA and the causes */
    public static PaReport updatePublicAcceptance(Location location, double[] monthlyMeans) {
        PAResult r = location.getPublicAcceptance().update(monthlyMeans);
        List<Integer> causes = new ArrayList<>();
        for (String cause : r.getCauses()) {
            int index = causeToIndicator(cause);
            if (index >= 0) {
                causes.add(index);
            }
        }
        return new PaReport(r.getPa(), r.getDelta(), causes);
    }

    /** the improvement of PA through medium-term measures is applied by the simulation */
    public static void increasePublicAcceptance(Location location, double gain) {
        location.getPublicAcceptance().increase(gain);
    }

    public static double publicAcceptanceValue(Location location) {
        return location.getPublicAcceptance().getValue();
    }

    /** translates the cause names of the PA object (e.g. "Temp", "CO2", "Fine dust") into indicator indexes */
    static int causeToIndicator(String cause) {
        String key = cause.toLowerCase().replaceAll("[^a-z0-9]", "");
        switch (key) {
            case "temp":
            case "temperature":
                return SimConstants.TEMP;
            case "water":
            case "waterlevel":
            case "waterlevels":
            case "waterreserve":
                return SimConstants.WATER;
            case "co2":
                return SimConstants.CO2;
            case "humidity":
                return SimConstants.HUMIDITY;
            case "finedust":
            case "finedustlevel":
            case "finedustlevels":
                return SimConstants.FINE_DUST;
            case "cloudiness":
            case "clouds":
                return SimConstants.CLOUDINESS;
            case "windspeed":
            case "wind":
                return SimConstants.WINDSPEED;
            default:
                return -1;
        }
    }

    // --------------------------------------------------------- Log output
    public static void logMonth(Location location, MonthRecord record) {
        location.getLogInformation().addMonth(record);
    }

    public static void logYear(Location location, int year, double climateCoefficient, YearBudget budget) {
        location.getLogInformation().addYear(year, climateCoefficient, budget);
    }
}
