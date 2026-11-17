package Aufgabe3;

/**
 * NOTE: Part: Vishwas (simulation, budget and climate updation), Simon
 * STYLE: procedural module (private constructor, only static constants and pure static functions).
 * Every tunable number of the simulation is here, so the model can be tuned in one place.
 */
public final class SimConstants {
    private SimConstants() {
    }

    // ---------------------------------------------------------- time
    public static final int YEARS = 100;
    public static final int MONTHS_PER_YEAR = 3;      // 3 months x 30 days = 90 days per year
    public static final int DAYS_PER_MONTH = 30;
    public static final int HISTORY_DAYS = 10;        // the last 10 days (0 to 10 entries) that the Weather gets

    // ---------------------------------------------------------- indicators
    // Index of each indicator inside the double[7] arrays that the simulation uses internally.
    public static final int TEMP = 0;
    public static final int WATER = 1;
    public static final int CO2 = 2;
    public static final int HUMIDITY = 3;
    public static final int FINE_DUST = 4;
    public static final int CLOUDINESS = 5;
    public static final int WINDSPEED = 6;
    public static final int INDICATOR_COUNT = 7;

    // BAD (procedural #6, global variable): public static final only fixes the reference of an array, any class can
    // still write INDICATOR_NAMES[i] (same for LONG_NAMES, LONG_COST_PER_LEVEL, LONG_LINKED_INDICATOR and
    // LONG_MITIGATION_PER_LEVEL). A wrong write would change the whole simulation without a trace. The arrays are
    // also parallel (same index = same measure). Better: an enum Indicator / LongTermMeasure with fields, or
    // List.of(...) (unmodifiable).
    public static final String[] INDICATOR_NAMES = {
            "Temp", "Water", "CO2", "Humidity", "Fine dust", "Cloudiness", "Windspeed"};

    // ERROR: the minimum and maximum of each indicator is stored twice (here and as MIN/MAX in Weather) and the only link is this comment. If one is changed, normalise and clamp would be wrong. Fix: Weather reads its ranges from here.
    // The simulation only uses them to normalise values (spike detection, long-term scores).
    private static final double[] INDICATOR_MIN = {15.0, -5.0, 350.0, 10.0, 5.0, 0.0, 0.0};
    private static final double[] INDICATOR_MAX = {37.0, 5.0, 800.0, 100.0, 150.0, 100.0, 100.0};

    // ------------------------------------------------------------ budget
    public static final double BASE_BUDGET = 1000.0;              // yearly budget B = BASE * (0.5 + wealth/100)
    public static final double WEALTH_BASE_FACTOR = 0.5;

    // connectivity (0-10) that makes the immediate measures cheaper
    public static final double CONNECTIVITY_COST_BASE = 1.5;
    public static final double CONNECTIVITY_COST_STEP = 0.05;

    // The higher limits of the immediate and medium, in share of B, long-term always gets the leftovers. long-term is guaranteed to have atleast 20% always.
    public static final double IMMEDIATE_CAP_BASE = 0.20;
    public static final double IMMEDIATE_CAP_PER_CONNECTIVITY_GAP = 0.02;  // per point below connectivity 10
    public static final double IMMEDIATE_CAP_PER_CLIMATE = 0.15;           // at start A = 1.7 (scaled linearly from 1.0)
    public static final double IMMEDIATE_CAP_MAX = 0.50;
    public static final double MEDIUM_CAP_BASE = 0.20;
    public static final double MEDIUM_CAP_PER_WEALTH = 0.10;               // at wealth 100
    public static final double START_A_MIN = 1.0;
    public static final double START_A_MAX = 1.7;

    // -------------------------------------------------- immediate measures
    // Spike detection (day to day change, normalised by the indicator range).
    // ERROR: the constraint "normal daily change and recovery step < SPIKE_THRESHOLD < smallest calamity shock" is not
    // always kept. Weather.changeIndicator clamps to MIN/MAX, so an indicator near its limit (e.g. humidity close to 100
    // at a flood, water close to -5 at a drought) changes by less than the nominal shock. CalamityDetector then
    // misses the calamity (matched < 2) or underestimates its severity. Cause: the contract only says what a calamity tries to do, not what it does.
    // Fix: Calamity.applyEffect (or Weather.changeIndicator) reports the shock that was really applied, or the
    // simulation reads the events from dailyCheck instead of detecting them from the spikes.
    public static final double SPIKE_THRESHOLD = 0.15;
    public static final double NOMINAL_SHOCK = 0.30;       // a "normal" calamity changes an indicator by 30% of its range
    public static final double SEVERITY_MIN = 0.5;
    public static final double SEVERITY_MAX = 2.0;
    public static final int SUPPRESS_DAYS = 2;             // days without detection after a detected calamity

    // Lump sum cost on the detection day = base * severity * connectivity multiplier.
    // base = old per-day cost (10 / 15 / 8) x about 7 calamity days.
    public static final double COST_DROUGHT = 70.0;
    public static final double COST_FLOOD = 105.0;
    public static final double COST_FLASH_FREEZE = 56.0;

    public static final int PROBABILITY_WINDOW_YEARS = 5;  // calamity track record used for long-term decisions

    // ----------------------------------------------------- medium measures
    // cost and public-acceptance gain of one purchase
    public static final double FESTIVAL_COST = 20.0;
    public static final double FESTIVAL_GAIN = 3.0;
    public static final double SHADING_COST = 40.0;
    public static final double SHADING_GAIN = 5.0;
    public static final double NATURAL_COOLING_COST = 90.0;
    public static final double NATURAL_COOLING_GAIN = 10.0;
    public static final double DISTRICT_COOLING_COST = 200.0;
    public static final double DISTRICT_COOLING_GAIN = 20.0;
    public static final double RESERVOIR_COST = 80.0;
    public static final double RESERVOIR_GAIN = 10.0;
    public static final double HEALTHCARE_COST = 100.0;
    public static final double HEALTHCARE_GAIN = 10.0;

    // ------------------------------------------------------ long measures
    // Order: CO2 tax, Solar, Windmills, Turbines/Dams, Heating infra
    public static final String[] LONG_NAMES = {"CO2 tax", "Solar", "Windmills", "Turbines/Dams", "Heating infra"};
    public static final int LT_CO2_TAX = 0;
    public static final int LT_SOLAR = 1;
    public static final int LT_WINDMILLS = 2;
    public static final int LT_DAMS = 3;
    public static final int LT_HEATING = 4;
    public static final double[] LONG_COST_PER_LEVEL = {200.0, 300.0, 300.0, 400.0, 450.0};
    // indicator each long-term measure is linked to (same order as LONG_NAMES), used for the Location's infrastructure
    public static final int[] LONG_LINKED_INDICATOR = {CO2, CLOUDINESS, WINDSPEED, WATER, TEMP};
    // reduction of the yearly growth factor of A per level (before saturation)
    public static final double[] LONG_MITIGATION_PER_LEVEL = {0.0010, 0.0008, 0.0008, 0.0005, 0.0006};
    public static final double LONG_SATURATION_LEVEL = 5.0;  // diminishing returns: m * L * (1 - exp(-level / L))
    public static final double LONG_WEIGHT_INDICATOR = 0.6;  // score = 0.6 * indicator + 0.4 * calamity risk
    public static final double LONG_WEIGHT_CALAMITY = 0.4;
    public static final double LONG_SCORE_FLOOR = 0.02;      // every measure gets a small share

    // ------------------------------------------------------------- climate
    public static final double A_MIN = 1.0;
    public static final double A_GROWTH_BASE = 1.005;        // yearly factor without CO2 effect and without measures
    public static final double A_CO2_FACTOR = 0.008;         // growth change per (CO2 mean / CO2_REF - 1)
    public static final double CO2_REF = 425.0;              // CO2 level at the start of the simulation (Weather starts at 425 ppm)

    // ------------------------------------------------------------- helpers
    // GOOD (procedural #3): pure static functions, no global state is read or written, the control flow has no branches.
    public static double range(int indicator) {
        return INDICATOR_MAX[indicator] - INDICATOR_MIN[indicator];
    }

    // maps an indicator value to 0...1
    public static double normalise(int indicator, double value) {
        return clamp((value - INDICATOR_MIN[indicator]) / range(indicator), 0.0, 1.0);
    }

    public static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ---------------------------------------------- infrastructure effect
    // How strongly built infrastructure pulls an affected indicator back toward normal each day.
    public static final double INFRA_STRENGTH_MAX = 0.20;    // saturates here, so weather still varies
    public static final double INFRA_STRENGTH_SCALE = 30.0;  // accumulated amount for ~63% of the max effect

    public static double infrastructureStrength(double amount) {
        if (amount <= 0.0) {
            return 0.0;
        }
        return INFRA_STRENGTH_MAX * (1.0 - Math.exp(-amount / INFRA_STRENGTH_SCALE));
    }

    // public acceptance scales the effectiveness of long-term measures between 0.5 and 1.0
    public static double paEffectiveness(double pa) {
        return 0.5 + clamp(pa, 0.0, 100.0) / 200.0;
    }
}
