import java.util.Map;

public class Test {
    private final double MaxTopTemp = 40;
    private final double MinTopTemp = -10;
    private double climateCoefficient; // A


    public static void main(String[] args) {
        LogInformation[] logInformations = new LogInformation[88];
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){
            for(int step = 0; step <= 7; step++, counter++) {
                logInformations[counter] = new LogInformation();
                BudgetSimulation.simulation(new Climate(1.0 + step / 10.0), startMiddleMeasureBudgetPart, logInformations[counter]);
            }
        }
        new Test().evaluation(logInformations);
    }

    public void evaluation(LogInformation[] logInformations) {
        int score = 200;
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){
            for(int step = 0; step <= 7; step++, counter++) {
                for (Integer day : logInformations[counter].getShortMeasureOvershoot().values()) {
                    score -= 10 - day/9;
                }


            }
        }
    }
}
