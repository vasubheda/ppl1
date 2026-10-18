import java.util.Random;
// This class carries out the 100 year budget simulation(each year has 90 days) combining climate measures and budget splitting. (Contains elements from the Climate, Weather and BudgetCalculator classes)
public class BudgetSimulation {
    static final int TOTAL_YEARS = 100;
    static final int DAYS_IN_YEAR = 90;

    // "simulation" takes A, M and LogInformation as input parameters, and writes A, decade averages and overshoots into the logs
    // Gets daily demands, overshoots(if any), yearly budget splits, and updates climate A yearly. Runs for 100 years (each year has 90 days).
    public static void simulation(Climate climate, int M, LogInformation log) {
        Weather weather = new Weather(new Random());
        BudgetCalculator cal = new BudgetCalculator(M);
        double decadeSum = 0;

        for (int y=1; y<=TOTAL_YEARS; y++) {
            weather.startNewYear();
            double sum=0;
            boolean overshot=false;
            for(int d = 1; d<=DAYS_IN_YEAR; d++){
                double A=climate.getA();
                sum+=cal.dailyDemand(weather.getTopTemp()*A, weather.getWaterReserve()*A);
                if(!overshot && sum >100-M){
                    overshot=true;
                    log.addShortMeasureOvershoot(y,d);
                }
                weather.nextDay();
            }
            YearBudget budget=cal.yearlySplit(sum);
            if (budget.getShortfall() > 0) {
                log.addShortfallAmount(y, budget.getShortfall());
            }
            climate.RecordYear(budget.getCause()/100);
            climate.UpdateA();

            decadeSum+= budget.getImmediate();
            if (y%10 == 0) {
                log.addAvgShortMeasure(decadeSum/10, y/10 -1);
                decadeSum = 0;
            }
        }
        log.setClimateCoefficient(climate.getA());
    }
}
