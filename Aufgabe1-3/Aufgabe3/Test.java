package Aufgabe3;

import java.util.List;
import java.util.function.Supplier;

/*Allgemeine Informationen
    Arbeitaufteilung:
    Simon: Evaluation, Infrastructure, Test, LogInformation, Location, SimConstants, TeamAdapter
    Vishwas: BudgetCalculator, BudgetSimulation, CalamityDetector, ClimateUpdater, LongTermPlanner, MediumTermPlanner, MonthRecord, SimConstants, TeamAdapter, YearBudget
    Jay: Calamity, Climate, Drought, FlashFreeze, Flood, PAResult, PublicAcceptance, Weather

    Simon Hahn Use of Ai:
    I used it to explain the LVA slides to me and how I should go about to solve the task Link: https://claude.ai/share/cf914a47-1f12-43c4-ae4d-8309972f7650
    For assignment 3 also used Claude to explain the Design-by-Contract concepts
    For the System.out.printf in ParallelRunner.benchmark how the output is formatted and which information to include

    Vishwas Vikram Bheda Use of Ai:
    - I used claude to translate the assignment in English, then I used ChatGPT to clear some doubts and approaches.
    - I used AI to understand how to use the Weather, Calamity, Tests, Evaluation, Infrastructure, Location, LogInformation, PublicAcceptence classes to integrate it into my simulation.

    Dhananjay Sharma Use of Ai:
    - I used claude to translate the assignment in English, then chatted with Claude to help clear my doubts on implementation.
    - Link - https://claude.ai/share/b0176886-1f03-4753-9223-7b309590c4e5

 */


 /*
    Modulisationunit of the class:
    Modul: the class is a modul because it is not used to create objects, it only starts the run from main
    Abstraktion: This class is responsible for starting the simulation of a location with the required starting parameters.
    After the simulation it gives the recorded LogInformation to the Evaluation, which scores how well the run went.
    STYLE: procedural. main is a short procedure in a module (static, no Test object is ever created):
    it runs three steps one after another: 1. build the location, 2. run the simulation, 3. evaluate the log.
    The control flow is a plain top-to-bottom sequence.
 */

import java.util.ArrayList;
import java.util.Random;

public class Test {

    /**
     * builds one location with its weather, climate, public acceptance and an (empty) infrastructur list
     * runs the 100 year simulation on it and then gives the recorded LogInformation to the Evaluation
     * which calculates the score of the run and prints the summary.
     * precondition: class {@link BudgetSimulation} with method {@link BudgetSimulation#simulation(Location)},
     * and class {@link Evaluation} with method {@link Evaluation#startEvaluation(LogInformation)}
     */
    public static void main(String[] args) {
        // pre parallel part:
        // GOOD (procedural): main is a flat top-to-bottom sequence, no global mutable state, each location independent.
        // BAD (procedural/copy-and-paste): the four-line block below is repeated ten times, only the
        // parameters differ. Better: keep the parameters in an array and loop once.
        /*
        Location location1 = new Location("Minas Tirith", 80, 8, new Weather(new Random(), shift(0, 0, 0, 0, 0)), new Climate(1.2), new ArrayList<>(), new LogInformation("Minas Tirith"));
        BudgetSimulation.simulation(location1);
        Evaluation.startEvaluation(location1.getLogInformation());
        FunctionalAnalysis.print(location1.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location2 = new Location("Hobbiton", 55, 4, new Weather(new Random(), shift(-2, 1, 5, 0, 0)), new Climate(1.0), new ArrayList<>(), new LogInformation("Hobbiton"));
        BudgetSimulation.simulation(location2);
        Evaluation.startEvaluation(location2.getLogInformation());
        FunctionalAnalysis.print(location2.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location3 = new Location("Erebor", 100, 3, new Weather(new Random(), shift(-13, 0, -10, 0, 0)), new Climate(1.9), new ArrayList<>(), new LogInformation("Erebor"));
        BudgetSimulation.simulation(location3);
        Evaluation.startEvaluation(location3.getLogInformation());
        FunctionalAnalysis.print(location3.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location4 = new Location("Edoras", 60, 7, new Weather(new Random(), shift(2, -1, 0, 0, 10)), new Climate(1.4), new ArrayList<>(), new LogInformation("Edoras"));
        BudgetSimulation.simulation(location4);
        Evaluation.startEvaluation(location4.getLogInformation());
        FunctionalAnalysis.print(location4.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location5 = new Location("Barad-dur", 20, 2, new Weather(new Random(), shift(12, -4, -25, 40, 0)), new Climate(2.7), new ArrayList<>(), new LogInformation("Barad-dur"));
        BudgetSimulation.simulation(location5);
        Evaluation.startEvaluation(location5.getLogInformation());
        FunctionalAnalysis.print(location5.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location6 = new Location("Rivendell", 75, 7, new Weather(new Random(), shift(-1, 2, 5, 0, 0)), new Climate(1.0), new ArrayList<>(), new LogInformation("Rivendell"));
        BudgetSimulation.simulation(location6);
        Evaluation.startEvaluation(location6.getLogInformation());
        FunctionalAnalysis.print(location6.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location7 = new Location("Esgaroth", 50, 9, new Weather(new Random(), shift(0, 3, 15, 0, 0)), new Climate(2.7), new ArrayList<>(), new LogInformation("Esgaroth"));
        BudgetSimulation.simulation(location7);
        Evaluation.startEvaluation(location7.getLogInformation());
        FunctionalAnalysis.print(location7.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location8 = new Location("Isengard", 70, 4, new Weather(new Random(), shift(4, 0, -5, 60, 0)), new Climate(2.1), new ArrayList<>(), new LogInformation("Isengard"));
        BudgetSimulation.simulation(location8);
        Evaluation.startEvaluation(location8.getLogInformation());
        FunctionalAnalysis.print(location8.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location9 = new Location("Khazad-dum", 90, 3, new Weather(new Random(), shift(-12, 0, -8, 0, 0)), new Climate(1.6), new ArrayList<>(), new LogInformation("Khazad-dum"));
        BudgetSimulation.simulation(location9);
        Evaluation.startEvaluation(location9.getLogInformation());
        FunctionalAnalysis.print(location9.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis

        Location location10 = new Location("Bree", 40, 6, new Weather(new Random(), shift(0, 0, 0, 0, 0)), new Climate(1.2), new ArrayList<>(), new LogInformation("Bree"));
        BudgetSimulation.simulation(location10);
        Evaluation.startEvaluation(location10.getLogInformation());
        FunctionalAnalysis.print(location10.getLogInformation());   // STYLE: functional part, see FunctionalAnalysis
        */

        //parallel part:

        Supplier<List<Location>> factory = () -> {
            List<Location> locs = new ArrayList<>();
            locs.add(new Location("Minas Tirith", 80, 8, new Weather(new Random(), shift(0, 0, 0, 0, 0)),    new Climate(1.2), new ArrayList<>(), new LogInformation("Minas Tirith")));
            locs.add(new Location("Hobbiton",     55, 4, new Weather(new Random(), shift(-2, 1, 5, 0, 0)),   new Climate(1.0), new ArrayList<>(), new LogInformation("Hobbiton")));
            locs.add(new Location("Erebor",      100, 3, new Weather(new Random(), shift(-13, 0, -10, 0, 0)),new Climate(1.9), new ArrayList<>(), new LogInformation("Erebor")));
            locs.add(new Location("Edoras",       60, 7, new Weather(new Random(), shift(2, -1, 0, 0, 10)),  new Climate(1.4), new ArrayList<>(), new LogInformation("Edoras")));
            locs.add(new Location("Barad-dur",    20, 2, new Weather(new Random(), shift(12, -4, -25, 40, 0)),new Climate(2.7), new ArrayList<>(), new LogInformation("Barad-dur")));
            locs.add(new Location("Rivendell",    75, 7, new Weather(new Random(), shift(-1, 2, 5, 0, 0)),   new Climate(1.0), new ArrayList<>(), new LogInformation("Rivendell")));
            locs.add(new Location("Esgaroth",     50, 9, new Weather(new Random(), shift(0, 3, 15, 0, 0)),   new Climate(2.7), new ArrayList<>(), new LogInformation("Esgaroth")));
            locs.add(new Location("Isengard",     70, 4, new Weather(new Random(), shift(4, 0, -5, 60, 0)),  new Climate(2.1), new ArrayList<>(), new LogInformation("Isengard")));
            locs.add(new Location("Khazad-dum",   90, 3, new Weather(new Random(), shift(-12, 0, -8, 0, 0)), new Climate(1.6), new ArrayList<>(), new LogInformation("Khazad-dum")));
            locs.add(new Location("Bree",         40, 6, new Weather(new Random(), shift(0, 0, 0, 0, 0)),    new Climate(1.2), new ArrayList<>(), new LogInformation("Bree")));
            return locs;
        };

        List<Location> locations = factory.get();
        ParallelRunner.simulateAllParallel(locations);          // paralleler Teil
        for (Location loc : locations) {
            Evaluation.startEvaluation(loc.getLogInformation());
            FunctionalAnalysis.print(loc.getLogInformation());
        }

        ParallelRunner.benchmark(factory);


    }

    /**
     * per-location normal shift: {temp, water, humidity, fineDust, wind}, mapped to SimConstants indexes
     */
    private static double[] shift(double temp, double water, double humidity, double fineDust, double wind) {
        return new double[]{temp, water, 0.0, humidity, fineDust, 0.0, wind};
    }
}
