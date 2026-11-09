package Aufgabe2;

import java.util.List;

/*
    Modulisationunit of the class:
    Object: all class variables are object because every simulated location is its own object with its own
    weather, climate, public acceptance, log and infrastructure
    Abstraktion: This class is responsible for holding one location together with its sub-objects and giving them out with getters.
    It does not run the simulation itself, the simulation reaches the sub-objects through this location (via TeamAdapter).
    STYLE: object-oriented. A location is an object that accumulates its sub-objects (weather, climate, public acceptance, log, infrastructure)
    as private fields and only gives them out through getters.
*/

/**
 * Class for one location that is simulated. It holds the fixed parameters of the location (name, wealth 0-100, connectivity 0-10) and its sub-objects
 * The sub-objects carry the changing state during the simulation: weather, climate, public acceptance, the log and the list of built infrastructure.
 * precondition: weather and climate are given from outside, the log and the public acceptance are created here with the start values.
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

    /**
     * creates a location. The log is created empty and the public acceptance starts at 50.
     * precondition: wealth in [0,100], connectivity in [0,10], weather and climate not null.
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
     */

    public void addInfrastructure(Infrastructure infrastructure) {
        this.infrastructure.add(infrastructure);
    }

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

    public List<Infrastructure> getInfrastructure() {
        return infrastructure;
    }

}
