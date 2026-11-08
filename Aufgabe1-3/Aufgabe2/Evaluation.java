package Aufgabe2;

/**
 * Gets an Array of 88 Objects from the class LogInformation
 * uses the information to calculate points and the best M over all Simulations
 * It gives the logInformations and the coresponding points for the best M to the method printSummary
 * precondition: Input Array of logInfomations not null (simulation already ran to put input in)
 */

public class Evaluation {

    public void startEvaluation(LogInformation logInformations) {
        double[] objective = new double[11];



        int best = 0;
        for (int i = 1; i < objective.length; i++) {
            if (objective[i] > objective[best]) {
                best = i;
            }
        }
    }
}