package Aufgabe2;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Part: Vishwas.
 *
 * THE ONLY PLACE where the simulation code touches classes of the teammates (Location, Weather,
 * PublicAcceptance, LogInformation, Infrastructure). If one of their methods changes, only this file has to be
 * adapted. The rest of the simulation works with plain values:
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

    // ------------------------------------------------------------ Location
    public static int wealth(Location location) {
        return location.getWealth();
    }

    public static int connectivity(Location location) {
        return location.getConnectivity();
    }

    public static Climate climate(Location location) {
        return location.getClimate();
    }

    // ------------------------------------------------------ Infrastructure
    /**
     * Adds an investment to the infrastructure of the location. The location already has infrastructure, so an
     * entry with the same name (ignoring case) is built on: for every linked indicator, amount is added to its
     * value in the map of affected indicators. Only if no entry with this name exists, a new one is created.
     *
     * @param measureName name of the bought measure (e.g. "Water reservoir")
     * @param indicators  indicator indexes (SimConstants.TEMP ...) the measure is linked to
     * @param amount      what was added: purchases of a medium-term measure, new whole levels of a long-term one
     */
    public static void addInfrastructure(Location location, String measureName, int[] indicators, int amount) {
        if (indicators.length == 0 || amount <= 0) {
            return;
        }
        List<Infrastructure> all = location.getInfrastructure();
        for (int i = 0; i < all.size(); i++) {
            Infrastructure existing = all.get(i);
            if (!existing.getName().equalsIgnoreCase(measureName)) {
                continue;
            }
            Map<String, Integer> affected = existing.getAffectedIndicators();
            try {
                for (int indicator : indicators) {
                    affected.merge(SimConstants.INDICATOR_NAMES[indicator], amount, Integer::sum);
                }
            } catch (UnsupportedOperationException readOnlyMap) {
                // the map of the existing entry can not be changed, so the entry is replaced by an extended copy
                Map<String, Integer> copy = new HashMap<>(affected);
                for (int indicator : indicators) {
                    copy.merge(SimConstants.INDICATOR_NAMES[indicator], amount, Integer::sum);
                }
                all.set(i, new Infrastructure(existing.getName(), copy));
            }
            return;
        }
        Map<String, Integer> affected = new HashMap<>();
        for (int indicator : indicators) {
            affected.put(SimConstants.INDICATOR_NAMES[indicator], amount);
        }
        location.addInfrastructure(new Infrastructure(measureName, affected));
    }

    // ------------------------------------------------------------- Weather
    /**
     * Lets the weather of the location create the next day. The simulation gives the last 0..10 days
     * (oldest first, each as double[7]) and the climate coefficient.
     */
    public static void advanceWeather(Location location, List<double[]> last10Days, double climateCoefficient) {
        location.getWeather().nextDay(last10Days, climateCoefficient);
    }

    /**
     * Lets each calamity check for today and possibly shock the weather of the location.
     * The calamity objects own their own formulas cooldowns and effects the simulation only triggers the daily check.
     */
    public static void advanceCalamities(Location location, List<Calamity> calamities, double climateCoefficient, Random random) {
        Weather weather = location.getWeather();
        for (Calamity calamity : calamities) {
            calamity.dailyCheck(weather, climateCoefficient, random);
        }
    }

    /**
     * Lets the built infrastructure of the location influence the weather: every infrastructure pulls the
     * indicators it affects a bit back toward normal (resilience). The more was invested in a measure, the
     * stronger but saturating the effect (SimConstants.infrastructureStrength), so infrastructure makes the
     * weather calmer without ever fully controlling it.
     */
    public static void applyInfrastructure(Location location, double climateCoefficient) {
        Weather weather = location.getWeather();
        for (Infrastructure infra : location.getInfrastructure()) {
            for (Map.Entry<String, Integer> entry : infra.getAffectedIndicators().entrySet()) {
                int index = causeToIndicator(entry.getKey());
                if (index < 0) {
                    continue;
                }
                double strength = SimConstants.infrastructureStrength(entry.getValue());
                weather.stabilize(index, strength, climateCoefficient);
            }
        }
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
