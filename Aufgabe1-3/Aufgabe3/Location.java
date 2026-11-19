package Aufgabe3;

import java.util.List;

/*
    Modulisationunit of the class:
    Object: all class variables are object because every simulated location is its own object with its own
    weather, climate, public acceptance, log and infrastructure

    Abstraktion: This class is responsible for holding one location together with its sub-objects and giving them out with getters.
    It does not run the simulation itself, the simulation reaches the sub-objects through this location (via TeamAdapter).

    STYLE: object-oriented. A location is an object that accumulates its sub-objects (weather, climate, public acceptance, log, infrastructure)
    as private fields and only gives them out through getters.

    Invariant: wealth is [0,100]; connectivity [0,10];
    sub objects (weather, logInformation, climatel, publicAcceptance) not null and only defined one time, after the constructer not changed.

    History-Constrains: Infrastracture List can only add more and not delete Infrastracture.

    GOOD(weak coupling/high cohesion): The location only manages the layout and outputs sub-objects, the simulation runs elsewhere.
*/

/**
 * Class for one location that is simulated. It holds the fixed parameters of the location (name, wealth 0-100, connectivity 0-10) and its sub-objects
 * The sub-objects carry the changing state during the simulation: weather, climate, public acceptance, the log and the list of built infrastructure.
 * precondition: weather, climate, Infrastructure List and LogInformation are given from outside, the public acceptance is created here with the start value.
 */

public class Location {
    private String name;
    private int wealth;
    private int connectivity;

    //sub-objects
    private Weather weather;
    private LogInformation logInformation;
    private Climate climate;
    private PublicAcceptance publicAcceptance;
    private List<Infrastructure> infrastructure;

    /*
        ERROR: the simulation (client, BudgetSimulation.simulation) requires the passed-in log to be empty but this constructor neither promises nor checks that.
        It only works because Test always passes a fresh LogInformation.
        Cause: the empty-log expectation lives only in the client, not in this server.
        Fix: either add "logInformation is empty" as a precondition here and check it
        or let Location create its own empty LogInformation(name) instead of taking one as a parameter.
    */

    /**
     * creates a location. The log is created empty and the public acceptance starts at 50.
     * precondition: wealth in [0,100], connectivity in [0,10], weather, climate, LogInformation and Infrastructure not null.
     * postcondition: publicAcceptance starts at 50
     */

    public Location(String name, int wealth, int connectivity, Weather weather, Climate climate, List<Infrastructure> infrastructure, LogInformation logInformation) {
        this.name = name;
        this.wealth = wealth;
        this.connectivity = connectivity;
        this.weather = weather;
        this.logInformation = logInformation;
        this.publicAcceptance = new PublicAcceptance(50);
        this.climate = climate;
        this.infrastructure = infrastructure;
    }

    /**
     * adds one built infrastructure to the location (e.g. a water reservoir built by a long-term measure)
     * precondition: infrastructure not null
     * postcondition: infrastructure is added to the infrastructure List (size+1)
     */

    public void addInfrastructure(Infrastructure infrastructure) {
        this.infrastructure.add(infrastructure);
    }

    /*
        The following getters are pure read-only queries.
        Postcondition of each: it returns the matching field and does not change the object.
        getInfrastructure() is the exception that breaks this read-only spirit by handing out the live list (see BAD comment)
    */

    public String getName() {
        return name;
    }

    public int getWealth() {
        return wealth;
    }

    public int getConnectivity() {
        return connectivity;
    }

    public Weather getWeather() {
        return weather;
    }

    public LogInformation getLogInformation() {
        return logInformation;
    }

    public Climate getClimate() {
        return climate;
    }

    public PublicAcceptance getPublicAcceptance() {
        return publicAcceptance;
    }

    /*
        BAD(Encapsulation/Aliasing): It returns the internal list directly, which means a client can modify the location objects itself.
        Better:
        public List<Infrastructure> getInfrastructure() {
            return infrastructure.stream().map(i -> new Infrastructure(i.getName(), new HashMap<>(i.getAffectedIndicators()))).collect(Collectors.toUnmodifiableList());
        }
    */

    public List<Infrastructure> getInfrastructure() {
        return infrastructure;
    }

}
