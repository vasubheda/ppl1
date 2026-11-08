package Aufgabe2;

import java.util.List;
import java.util.Map;

/**
 * Gets an Array of 88 Objects from the class LogInformation
 * uses the information to calculate points and the best M over all Simulations
 * It gives the logInformations and the coresponding points for the best M to the method printSummary
 * precondition: Input Array of logInfomations not null (simulation already ran to put input in)
 */

public class Evaluation {




    public void Evaluation() {

    }

    public void startEvaluation(LogInformation[] logInformations) {
        double[] objective = new double[11];
        for(LogInformation logInformation : logInformations) {
            int points = 1000;
            for (double amount : logInformation.getShortfallAmount().values()) {
                points -= 5 + (int) (amount/5.0);
            }
            for (double avg : logInformation.getAvgShortMeasure()) {
                points -= (int) (avg/2.0);
            }
            points -= (int) (100.0 * (logInformation.getClimateCoefficient() - 1.0));
            if(points < 0) {
                points = 0;
            }
            objective[logInformation.getStartMiddleMeasureBudgetPart() / 10] += points / 8.0;
            int score = points/100;
            System.out.println("Die Simulation mit Anfangs A:" + logInformation.getStartClimateCoefficient() + ", M:" + logInformation.getStartMiddleMeasureBudgetPart() + ", erzielte eine Punktzahl:" + points + " von möglichen 1000 welches einen score von:" + score + " ergibt [0-10]");*/
        }
        int best = 0;
        for (int i = 1; i < objective.length; i++) {
            if (objective[i] > objective[best]) {
                best = i;
            }
        }
    }
}