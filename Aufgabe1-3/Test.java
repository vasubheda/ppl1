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
        int points;
        int score =;
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){
            for(double startClimateCoefficient = 1.0;startClimateCoefficient<=1.7;startClimateCoefficient+=0.1, counter++) {
                points = 200;
                for (int day : logInformations[counter].getShortMeasureOvershoot().values()) {
                    points -= 10 - day/18;
                }
                for (double avg : logInformations[counter].getAvgShortMeasure()) {
                    points -= (int) (avg/5.0);
                }

                points -= (int) (40.0 * (logInformations[counter].getClimateCoefficient() - 1.0));
                score = points/20;
                System.out.println("Die Simulation mit Anfangs A:" + startClimateCoefficient + ", M:" + startMiddleMeasureBudgetPart + ", erzielte eine Punktzahl:" + points + " von möglichen 200 welches einen score von:" + score + " ergibt");
            }
        }
    }
}
