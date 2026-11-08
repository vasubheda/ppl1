package Aufgabe2;

import Aufgabe1.YearBudget;

import java.util.Map;

/**
 * Part: Vishwas (budget).
 *
 * Handles the budget of ONE location for one simulation run (budget units, not percent).
 *  - yearly budget B depends on wealth, B = BASE * (0.5 + wealth/100)
 *  - ceilings (caps) for the immediate (short-term) and the medium-term measures depend on the location
 *    (connectivity, wealth and start climate coefficient), the long-term measures get the leftover
 *  - immediate pool: one pool per year, drawn on demand when a calamity happens
 *  - medium allowance: cap / 3 per month, unspent money rolls over to the next month of the same year
 *  - unspent immediate and medium money is not lost, it flows into the long-term budget at the year end
 *
 * Modularization unit: object (state per location and run). Not responsible for deciding WHAT to buy,
 * only for how much money is there and for the costs.
 */
public class BudgetCalculator {
    private final double yearlyBudget;
    private final double immediateCap;      // absolute ceiling per year
    private final double mediumCap;         // absolute ceiling per year
    private final double connectivityCostMultiplier;

    private double immediateSpent;
    private double mediumSpent;
    private double mediumAvailable;
    private double unpaidImmediate;         // internal only: calamity costs that did not fit in the immediate pool

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

    /** resets the pools, to be called at the start of each year */
    public void startYear() {
        immediateSpent = 0.0;
        mediumSpent = 0.0;
        mediumAvailable = 0.0;
    }

    /** adds the allowance of one month for the medium-term measures (unspent money of earlier months stays) */
    public void startMonth() {
        mediumAvailable += mediumCap / SimConstants.MONTHS_PER_YEAR;
    }

    /** lump sum cost of a detected calamity: base cost x severity x connectivity multiplier */
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

    /**
     * Pays a short-term (immediate) measure from the immediate pool.
     * Never pays more than the pool has left. Returns the amount that was really paid.
     */
    public double spendImmediate(double cost) {
        double room = Math.max(0.0, immediateCap - immediateSpent);
        double paid = Math.min(cost, room);
        immediateSpent += paid;
        unpaidImmediate += cost - paid;
        return paid;
    }

    /** buys a medium-term measure if the allowance covers it, returns whether it was bought */
    public boolean trySpendMedium(double cost) {
        if (cost > mediumAvailable + 1e-9) {
            return false;
        }
        mediumAvailable -= cost;
        mediumSpent += cost;
        return true;
    }

    /** what is left for the long-term measures at the year end (budget minus what was really spent) */
    public double remainder() {
        return Math.max(0.0, yearlyBudget - immediateSpent - mediumSpent);
    }

    /** builds the year-end summary, longTermAllocation is how the remainder was split on the long-term measures */
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

    /** internal only (not logged): sum of calamity costs that exceeded the immediate pool over the whole run */
    public double getUnpaidImmediate() {
        return unpaidImmediate;
    }
}
