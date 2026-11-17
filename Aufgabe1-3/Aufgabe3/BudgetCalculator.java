package Aufgabe3;

import java.util.Map;

// NOTE: Part: Vishwas (budget)
// STYLE: object-oriented. A BudgetCalculator is an object that owns the money pools of ONE location
// (private fields, changed only through its methods).
// GOOD (OO #1, class cohesion): every budget rule (yearly budget, caps, monthly allowance, calamity cost,
// remainder) lives in this one class, so BudgetSimulation does not know a single formula. We considered
// computing the costs and caps directly in BudgetSimulation, but then a change of the budget model would
// touch the simulation loop as well.
/**
 * Manages the budget of ONE location for ONE simulation run.
 * Yearly budget B = BASE_BUDGET * (WEALTH_BASE_FACTOR + wealth/100). The immediate pool (cap) is drawn on demand,
 * the medium pool gives an allowance of mediumCap/MONTHS_PER_YEAR per month (unspent money rolls over inside the
 * year), the long-term measures get the remainder.
 *
 * Class invariant (holds after the constructor and after every public method):
 *   0 <= immediateSpent <= immediateCap, mediumSpent >= 0, mediumAvailable >= 0 (up to 1e-9),
 *   immediateCap + mediumCap <= 0.8 * yearlyBudget (so the long-term share is at least 20 %),
 *   immediateSpent + mediumSpent <= yearlyBudget.
 * History constraint: per year the calls come in the order startYear(), then startMonth() once per month,
 * then closeYear(). trySpendMedium has no allowance before the first startMonth() of a year.
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

    /**
     * precondition: wealth in [0,100], connectivity in [0,10], startA >= 0 (normally START_A_MIN..START_A_MAX).
     * Violating wealth or connectivity throws IllegalArgumentException.
     * postcondition: the invariant of the class holds, all pools are empty.
     */
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

    /**
     * precondition: none (may be called at any time, but the simulation calls it once at the start of every year).
     * postcondition: immediateSpent = mediumSpent = mediumAvailable = 0. The total of unpaid calamity costs is kept.
     */
    public void startYear() {
        immediateSpent = 0.0;
        mediumSpent = 0.0;
        mediumAvailable = 0.0;
    }

    /**
     * precondition: startYear() was called in this year; called once per month.
     * postcondition: mediumAvailable grew by mediumCap / MONTHS_PER_YEAR (earlier unspent money stays).
     */
    public void startMonth() {
        mediumAvailable += mediumCap / SimConstants.MONTHS_PER_YEAR;
    }

    // BAD (OO #2, dynamic binding missing): the cost of a calamity is chosen with a switch on the type enum, and
    // "default" silently means flash freeze. A new calamity needs a change here, in CalamityDetector.evaluate and in
    // LongTermPlanner. This came from the detector only reporting an enum, not an object. Better: the Calamity
    // (or the Type) provides its own base cost, so dynamic binding replaces the switch. It also makes
    // BudgetCalculator depend on CalamityDetector.Event (coupling from budget to detection).
    /**
     * precondition: event != null.
     * postcondition: result >= 0, = base cost of the type * event.severity * connectivityCostMultiplier.
     */
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
     * precondition: cost >= 0.
     * postcondition: 0 <= result <= cost, result is what was really paid. The pool is never overdrawn
     * (immediateSpent <= immediateCap), the part that could not be paid is added to unpaidImmediate.
     */
    public double spendImmediate(double cost) {
        double room = Math.max(0.0, immediateCap - immediateSpent);
        double paid = Math.min(cost, room);
        immediateSpent += paid;
        unpaidImmediate += cost - paid;
        return paid;
    }

    /**
     * precondition: cost >= 0.
     * postcondition: returns true exactly when cost was taken from the allowance (mediumAvailable and mediumSpent
     * changed by cost). Returns false and changes nothing when the allowance (+1e-9) does not cover the cost.
     */
    public boolean trySpendMedium(double cost) {
        if (cost > mediumAvailable + 1e-9) {
            return false;
        }
        mediumAvailable -= cost;
        mediumSpent += cost;
        return true;
    }

    /**
     * postcondition: result = max(0, yearlyBudget - immediateSpent - mediumSpent) >= 0, no state is changed.
     * Because of the caps the result is at least 20 % of the yearly budget.
     */
    public double remainder() {
        return Math.max(0.0, yearlyBudget - immediateSpent - mediumSpent);
    }

    /**
     * precondition: the sum of the values of longTermAllocation equals remainder() (up to rounding).
     * postcondition: the returned YearBudget has immediate + medium + longTerm = yearlyBudget. The state is not
     * reset, the next year starts with startYear().
     */
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
