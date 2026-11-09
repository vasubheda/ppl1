package Aufgabe2;

import java.util.Map;

/**
 * Part: Vishwas (budget)
 * It handles the budget of 1 location for 1 simulation run.
 * - And the yearly budget B depends on the wealth of that specific location. We dynamically calculate it by the formula B = BASE * (0.5 +wealth/100)
 * - There are maximum values capped for immediate(short-term) and the medium-term measures using the same logic of dynamically calculating the cap dependent on the location (connectivity, wealth and start climate coefficient).
 * - The long-term measures get the leftover budget.
 * - We have 1 immediate pool per year, drawn on demand when a calamity happens.
 * - medium allowance : cap/3 per month, unspent money rolls over to the next month of the same year.
 * - and, unspent immediate and medium money is not lost, it flows into the long-term budget at the year end.
 *
 */
public class BudgetCalculator {
    private final double yearlyBudget;
    private final double immediateCap;      // absolute immediate cap per year
    private final double mediumCap;         // absolute medium cap per year
    private final double connectivityCostMultiplier;

    private double immediateSpent;
    private double mediumSpent;
    private double mediumAvailable;
    private double unpaidImmediate;         // The calamity costs that the immediate budget was not able to fulfill, this is internal only for now, not logged anywhere.

    public BudgetCalculator(int wealth, int connectivity, double startA) {
        if (wealth < 0 || wealth > 100) {
            throw new IllegalArgumentException("wealth must be in range [0,100]");
        }
        if (connectivity < 0 || connectivity > 10) {
            throw new IllegalArgumentException("connectivity must be in range [0,10]");
        }
        this.yearlyBudget = SimConstants.BASE_BUDGET * (SimConstants.WEALTH_BASE_FACTOR + wealth / 100.0);

        double climateShare = SimConstants.clamp((startA - SimConstants.START_A_MIN)
                / (SimConstants.START_A_MAX - SimConstants.START_A_MIN), 0.0, 1.0);
        double immediateShare = SimConstants.IMMEDIATE_CAP_BASE
                + SimConstants.IMMEDIATE_CAP_PER_CONNECTIVITY_GAP * (10 - connectivity)
                + SimConstants.IMMEDIATE_CAP_PER_CLIMATE * climateShare;
        immediateShare = Math.min(immediateShare, SimConstants.IMMEDIATE_CAP_MAX);
        double mediumShare = SimConstants.MEDIUM_CAP_BASE + SimConstants.MEDIUM_CAP_PER_WEALTH * wealth / 100.0;

        this.immediateCap = yearlyBudget * immediateShare;
        this.mediumCap = yearlyBudget * mediumShare;
        this.connectivityCostMultiplier = SimConstants.CONNECTIVITY_COST_BASE
                - SimConstants.CONNECTIVITY_COST_STEP * connectivity;
    }

    // This resets the pools when called at the start of each year.
    public void startYear() {
        immediateSpent = 0.0;
        mediumSpent = 0.0;
        mediumAvailable = 0.0;
    }

    // This provides the allowance of one month for the medium-term measures(and the unspent money of earlier months stays.)
    public void startMonth() {
        mediumAvailable += mediumCap / SimConstants.MONTHS_PER_YEAR;
    }

    // Calculates the full cost (large amount) of a detected calamity, base cost x severity x connectivity multiplier.
    public double calamityCost(CalamityDetector.Event event) {
        double base;
        switch (event.getType()) {
            case DROUGHT:
                base = SimConstants.COST_DROUGHT;
                break;
            case FLOOD:
                base = SimConstants.COST_FLOOD;
                break;
            default:
                base = SimConstants.COST_FLASH_FREEZE;
                break;
        }
        return base * event.getSeverity() * connectivityCostMultiplier;
    }

    // This then pays the short-term measure from the immediate pool. It never pays more than the pool has left, and the function returns the amount that was paid.
    public double spendImmediate(double cost) {
        double room = Math.max(0.0, immediateCap - immediateSpent);
        double paid = Math.min(cost, room);
        immediateSpent += paid;
        unpaidImmediate += cost - paid;
        return paid;
    }

    // This buys a medium-term measure, if the allowance covers it. Returns whether it was bought.
    public boolean trySpendMedium(double cost) {
        if (cost > mediumAvailable + 1e-9) {
            return false;
        }
        mediumAvailable -= cost;
        mediumSpent += cost;
        return true;
    }

    // Calculates the remainder (what is left for the long-term measures, budget minus what was really spent)
    public double remainder() {
        return Math.max(0.0, yearlyBudget - immediateSpent - mediumSpent);
    }

    // This builds the year-end summary, longTermAllocation is how the remainder was split on the long-term measures.
    public YearBudget closeYear(Map<String, Double> longTermAllocation) {
        return new YearBudget(immediateSpent, mediumSpent, remainder(), longTermAllocation);
    }

    public double getYearlyBudget() {
        return yearlyBudget;
    }

    public double getImmediateCap() {
        return immediateCap;
    }

    public double getMediumCap() {
        return mediumCap;
    }

    public double getMediumAvailable() {
        return mediumAvailable;
    }

    // This is the sum of calamity costs that exceeded the immediate budget, over the whole run. (Not logged, internal only)
    public double getUnpaidImmediate() {
        return unpaidImmediate;
    }
}
