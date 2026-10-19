import java.util.List;
import java.util.Map;


/*Allgemeine Informationen
    Simon Hahn Use of Ai: I used it to explain the LVA slides too me how I should I go about to solve the task Link: https://claude.ai/share/b80245ae-9583-4b03-9ffa-60460b6ecada


 */


/* Modularisierungseinheiten dieser Klasse:


 */

public class Test {

    /**
     * uses every single start value that is needed and the current LogInfomation to record the results
     * [A:1-1.7 in 0.1 steps]
     * [M:0-100 in 10 steps]
     * [88 times in whole]
     * and initializises simulation of BudgetSimulation
     * after every simulation 9is over it gives an 88 long Array of Objects from the class LogInfomation to the evalution method
     * precondition: class {@link BudgetSimulation} with method {@link BudgetSimulation#simulation(Climate, int, LogInformation)}, and method {@link #evaluation(LogInformation[])}
     */

    public void main() {
        LogInformation[] logInformations = new LogInformation[88];
        int counter = 0;
        for(int startMiddleMeasureBudgetPart = 0;startMiddleMeasureBudgetPart <=100;startMiddleMeasureBudgetPart+=10){ // M loop
            for(int startClimateCoefficient = 10;startClimateCoefficient<=17;startClimateCoefficient++, counter++) { // A Loop
                logInformations[counter] = new LogInformation( (double) (startClimateCoefficient/10.0),  startMiddleMeasureBudgetPart);
                BudgetSimulation.simulation(new Climate((double) startClimateCoefficient/10), startMiddleMeasureBudgetPart, logInformations[counter]);
            }
        }
        evaluation(logInformations);
    }

    /**
     * Gets an Array of 88 Objects from the class LogInformation
     * uses the information to calculate points and on the basis of the points it gets a score from 0-10
     * It Displays the starting parameters and the coresponding points and scores
     * precondition: Input Array of logInfomations
     */

    public void evaluation(LogInformation[] logInformations) {
        int points;
        int score;
        for(LogInformation logInformation : logInformations) {
            points = 1000;
            for (int day : logInformation.getShortMeasureOvershoot().values()) {
                points -= 10 - day/18;
            }
            for (double avg : logInformation.getAvgShortMeasure()) {
                points -= (int) (avg/2.0);
            }
            points -= (int) (100.0 * (logInformation.getClimateCoefficient() - 1.0));
            if(points < 0) {
                points = 0;
            }
            score = points/100;
            System.out.println("Die Simulation mit Anfangs A:" + logInformation.getStartClimateCoefficient() + ", M:" + logInformation.getStartMiddleMeasureBudgetPart() + ", erzielte eine Punktzahl:" + points + " von möglichen 1000 welches einen score von:" + score + " ergibt [0-10]");
        }
    }
}
