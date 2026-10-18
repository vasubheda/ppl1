import java.util.ArrayList;
import java.util.List;

/**
 * Done by Dhananjay Sharma
 * AI use - Cluade chat(conversion mode) is used starting from the translating the excercise and course Script to English till the end untill
 * I fully understand theexercise. I shared my plan which was discussed with grp members before.
 * AI suggested the numeric constants in updateA() - the growth and reduction values
 *
 * Represents the climate warming as a single factor A that grows or shrinks
 * depending on how much of the budget went into fighting the causes.
 *
 * Module part: the constants, because they are the fixed rules of how A reacts
 * and never change between runs. Object part: the current A and the history of
 * cause-fighting shares with the methods that use them, because this state
 * changes year by year.
 *
 * Outside its responsibility: weather values, the parameter M, budgets and
 * demand for immediate measures, and deciding when a year ends. The simulation
 * decides when to record a share and when to update A.
 */

public class Climate {
    // Years of history that count towards the long term average (LX)
    private static final int WINDOW_YEARS = 10;
    // Yearly change of A if nothing goes into cause fighting (A grows)
    private static final double GROWTH_WITHOUT_CAUSE = 1.005;
    // How strongly cause fighting slows or reverse the growth of A
    private static final double REDUCTION_PER_SHARE = 0.008;
    // Warming never turns into cooling below the normal weather
    private static final double A_MIN = 1.0;
    private double a;
    private final List<Double> CauseShares = new ArrayList<>();

    /**
     * Represents the starting state of the climate.
     * Takes the initial climate factor A and gives back a climate without history.
     */
    public Climate(double StartA) {
        this.a = StartA;
    }

    /**
     * Represents the current state of warming.
     * Takes nothing and gives back the climate factor A as of now.
     */
    public double getA() {
        return a;
    }

    /**
     * Represents memory of what was invested into the causes.
     * Takes the share of the yearly budget 0.0 to 1.0 spent on cause fighting in the year that just ended and gives back nothing.
     */
    public void RecordYear(double CauseShare) {
        CauseShares.add(CauseShare);
    }

    /**
     * Represents the yearly reaction of the climate to past causefighting.
     * Takes nothing but uses the recorded shares of the last ten years (LX - long term avarage);
     * gives back nothing, A is changed: more cause fighting means A grows slower or shrinks.
     */
    public void UpdateA() {
        int count = Math.min(WINDOW_YEARS, CauseShares.size());
        double sum = 0.0;
        for (int i = CauseShares.size() - count; i < CauseShares.size(); i++){
            sum += CauseShares.get(i);
        }
        double lx = sum / count;
        double factor = GROWTH_WITHOUT_CAUSE - REDUCTION_PER_SHARE * lx;
        a = Math.max(A_MIN, a * factor);
    }
}