//Handles Budget for one simulation run and M is constant for the whole run.

public class BudgetCalculator {

    static final double T0=36.0; // max tolerated temperature at M = 0
    static final double At=0.1; //per percent of M
    static final double W0=-4.0;//min tolerated water reserve at M=0
    static final double Bw = 0.1;//water units per percent of M

    //weights: percentage of yearly budget per day per unit of access.
    static final double K1=0.04;
    static final double K2=0.04;
    private final double M;

    // Thresholds: tMax and wMin (larger M -> larger tolerated range)
    private final double tMax;
    private final double wMin;

    public BudgetCalculator(double M){
        if(M<0 || M >100) {
            throw new IllegalArgumentException("M must be in range [0,100]");
        }
        this.M=M;
        this.tMax = T0+At*M;
        this.wMin = W0 - Bw*M;
    }

    // Calculates daily needs for immediate measures (percent of yearly budget) from adjusted Temperature and Adjusted water reserve.
    public double dailyDemand(double temp, double water){
        double overHeat = Math.max(0, temp-tMax);
        double Dryness =  Math.max(0, wMin - water);
        return K1*overHeat + K2*Dryness;
    }

    // Splits the yearly budget where the summ of daily demands is given.
    public YearBudget yearlySplit(double sumOfDailyDemands){
        double available= 100-M;
        double immediate = Math.min(available, sumOfDailyDemands);
        double shortfall = Math.max(0, sumOfDailyDemands-available);
        double cause = available - immediate;
        return new YearBudget(immediate, cause,shortfall);
    }
}
