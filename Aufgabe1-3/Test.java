import java.util.Map;

public class Test {
    private final double MaxTopTemp = 40;
    private final double MinTopTemp = -10;
    private double climateCoefficient; // A


    public void main() {
        LogInformation[] logInformations = new LogInformation[88];
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){
            for(double startClimateCoefficient = 1.0;startClimateCoefficient<=1.7;startClimateCoefficient+=0.1, counter++) {
                this.climateCoefficient=startClimateCoefficient;
                simulation(new Climate(climateCoefficient), startMiddleMeasureBudgetPart, logInformations[counter]);
            }
        }
        evaluation(logInformations);
    }

    public void evaluation(LogInformation[] logInformations) {
        int score = 200;
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){
            for(double startClimateCoefficient = 1.0;startClimateCoefficient<=1.7;startClimateCoefficient+=0.1, counter++) {
                for (Integer day : logInformations[counter].getShortMeasureOvershoot().values()) {
                    score -= 10 - day/9;
                }


            }
        }
    }
}
