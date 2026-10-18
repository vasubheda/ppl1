public class Test {
    private final double MaxTopTemp = 40;
    private final double MinTopTemp = -10;
    private double climateCoefficient; // A
    private LogInformation currentLogInformation;


    public void main() {
        LogInformation[] logInformations = new LogInformation[88];
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){
            for(double startClimateCoefficient = 1.0;startClimateCoefficient<=1.7;startClimateCoefficient+=0.1, counter++) {
                this.climateCoefficient=startClimateCoefficient;
                simulation(randomWeather(), 100 , startMiddleMeasureBudgetPart);

            }
        }
    }
}
