package Aufgabe2;


/*Allgemeine Informationen
    Arbeitaufteilung:
    Simon: main, evaluation, LoginInformation
    Vishwas: Aufgabe1.YearBudget, Aufgabe1.BudgetSimulation, printEdgeCase, printSummary
    Jay: Aufgabe1.Climate, Aufgabe1.Weather

    Simon Hahn Use of Ai: I used it to explain the LVA slides to me and how I should go about to solve the task Link: https://claude.ai/share/b80245ae-9583-4b03-9ffa-60460b6ecada

    Vishwas Vikram Bheda Use of Ai:
    - I used claude to translate the assignment in Englih, then I used ChatGPT to clear some doubts and approaches.
    - I used AI to understand how to use the Aufgabe1.Weather.java and Aufgabe1.Climate.java classes to integrate it into my simulation, the suggestions from AI include line 15, 19, 20, 25, 31, 32
    - I used AI to help me format some print messages in printEdgeCase and printSummary in Aufgabe1.Test, so that the output is formatted consistently.
    Link to Claude : https://claude.ai/share/4b05c785-2a51-4e75-9cf7-d765185d23eb
    Link to ChatGPT : https://chatgpt.com/share/6ad5cd92-82b4-83eb-a6e7-051d9122f3a5

 */


 /*
    Modulisationunit of the class:
    Modul: the class is a modul because it not used to create objects
    Abstraktion: This class is responsible for starting the simulation processes with the required starting parameters.
    it also evaluates the result and finds the best M
 */

import java.util.ArrayList;
import java.util.List;

public class Test {

    /**
     * uses every single start value that is needed and the current LogInfomation to record the results
     * [A:1-1.7 in 0.1 steps]
     * [M:0-100 in 10 steps]
     * [88 times in whole]
     * and initializises simulation of Aufgabe1.BudgetSimulation
     * after every simulation is over it gives an 88 long Array of Objects from the class LogInfomation to the evalution method
     * precondition: class {@link Aufgabe1.BudgetSimulation} with method {@link Aufgabe1.BudgetSimulation#simulation(Aufgabe1.Climate, int, Aufgabe1.LogInformation)}, and method {@link #evaluation(Aufgabe1.LogInformation[])}
     */

    public static void main(String[] args) {

        Location location1 = new Location("Wien", 80, 9, new Weather(), new Climate(1), new ArrayList<>());

        BudgetSimulation.simulation(location1);
    }
}
