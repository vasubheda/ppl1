import java.util.List;
import java.util.Map;


/*Allgemeine Informationen
    Arbeitaufteilung:
    Simon: main, evaluation, LoginInformation
    Vishwas: YearBudget, BudgetSimulation, printEdgeCase, printSummary
    Jay: Climate, Weather

    Simon Hahn Use of Ai: I used it to explain the LVA slides to me and how I should go about to solve the task Link: https://claude.ai/share/b80245ae-9583-4b03-9ffa-60460b6ecada

    Vishwas Vikram Bheda Use of Ai:
    - I used claude to translate the assignment in Englih, then I used ChatGPT to clear some doubts and approaches.
    - I used AI to understand how to use the Weather.java and Climate.java classes to integrate it into my simulation, the suggestions from AI include line 15, 19, 20, 25, 31, 32
    - I used AI to help me format some print messages in printEdgeCase and printSummary in Test, so that the output is formatted consistently.
    Link to Claude : https://claude.ai/share/4b05c785-2a51-4e75-9cf7-d765185d23eb
    Link to ChatGPT : https://chatgpt.com/share/6ad5cd92-82b4-83eb-a6e7-051d9122f3a5

 */


 /*
    Modulisationunit of the class:
    Modul: the class is a modul because it not used to create objects
    Abstraktion: This class is responsible for starting the simulation processes with the required starting parameters.
    it also evaluates the result and finds the best M
 */

public class Test {

    /**
     * uses every single start value that is needed and the current LogInfomation to record the results
     * [A:1-1.7 in 0.1 steps]
     * [M:0-100 in 10 steps]
     * [88 times in whole]
     * and initializises simulation of BudgetSimulation
     * after every simulation is over it gives an 88 long Array of Objects from the class LogInfomation to the evalution method
     * precondition: class {@link BudgetSimulation} with method {@link BudgetSimulation#simulation(Climate, int, LogInformation)}, and method {@link #evaluation(LogInformation[])}
     */

    public static void main(String[] args) {
        printEdgeCase(0, 1.0);
        printEdgeCase(100, 1.7);
        printEdgeCase(0, 1.7);

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
     * uses the information to calculate points and the best M over all Simulations
     * It gives the logInformations and the coresponding points for the best M to the method printSummary
     * precondition: Input Array of logInfomations not null (simulation already ran to put input in)
     */

    public static void evaluation(LogInformation[] logInformations) {
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
            /*int score = points/100;
            System.out.println("Die Simulation mit Anfangs A:" + logInformation.getStartClimateCoefficient() + ", M:" + logInformation.getStartMiddleMeasureBudgetPart() + ", erzielte eine Punktzahl:" + points + " von möglichen 1000 welches einen score von:" + score + " ergibt [0-10]");*/
        }
        int best = 0;
        for (int i = 1; i < objective.length; i++) {
            if (objective[i] > objective[best]) {
                best = i;
            }
        }
        printSummary(logInformations, best * 10, objective[best]);
    }

    /**
     * This prints the value ranges over all start values of A for the given M.
     * The condition is that logInformations must hold the runs of that M among others.
     */

    private static void printSummary(LogInformation[] logInformations, int m, double objective) {
        double aMin=Double.MAX_VALUE, aMax=0, fundsMin=Double.MAX_VALUE, fundsMax =0, shortMax =0;
        int yearsMin =Integer.MAX_VALUE, yearsMax = 0;
        for (LogInformation log : logInformations) {
            if (log.getStartMiddleMeasureBudgetPart() != m) {
                continue;
            }
            aMin =Math.min(aMin, log.getClimateCoefficient());
            aMax =Math.max(aMax, log.getClimateCoefficient());
            for (double avg: log.getAvgShortMeasure()) {
                fundsMin =Math.min(fundsMin, avg);
                fundsMax= Math.max(fundsMax, avg);
            }
            int years =log.getShortfallAmount().size();
            yearsMin =Math.min(yearsMin,years);
            yearsMax = Math.max(yearsMax,years);
            for (double amount : log.getShortfallAmount().values()) {
                shortMax=Math.max(shortMax, amount);
            }
        }
        System.out.println("Best M, according to the objective function: " + m + "% (objective value " + Math.round(objective) + " of 1000)");
        System.out.println("Summary over start values of A from 1.0 to 1.7 with M = " + m + "%:");
        System.out.printf(" A after 100 years: from %.3f to %.3f%n", aMin, aMax);
        System.out.printf(" Immediate funds per 10 years: from %.2f%% to %.2f%%%n", fundsMin, fundsMax);
        System.out.println(" Years with a shortfall: from " + yearsMin + " to " + yearsMax);
        System.out.printf(" Largest shortfall: %.2f%%%n", shortMax);
    }

    /**
     * It runs one simulation for the given M, start A and then prints A after 100 years.
     * The 10 decade averages of immediate funds and shortfalls are printed as well.
     */

    private static void printEdgeCase(int m, double a) {
        LogInformation log=new LogInformation(a, m);
        BudgetSimulation.simulation(new Climate(a), m, log);
        System.out.println("Edge case: M = " + m + "%, start A = " + a);
        System.out.printf("A after 100 years : %.3f%n", log.getClimateCoefficient());
        System.out.println("Immediate funds, average per 10 years (% of yearly budget):");
        double[] avg=log.getAvgShortMeasure();
        for (int i = 0;i < avg.length;i++) {
            System.out.printf(" years %3d-%3d: %6.2f%%%n", i*10 + 1, i*10 + 10, avg[i]);
        }
        if (log.getShortfallAmount().isEmpty()) {
            System.out.println("Shortfalls: none");
        } else {
            System.out.println("Shortfalls (need for immediate measures exceeds the budget):");
            for (int year =1;year<=100; year++) {
                Double amount = log.getShortfallAmount().get(year);
                if (amount != null) {
                    System.out.printf("  !!!year %3d: exceeded by %.2f%%%n", year, amount);
                }
            }
        }
        System.out.println();
    }
}
