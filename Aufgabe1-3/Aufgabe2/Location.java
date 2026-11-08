package Aufgabe2;

import java.util.List;

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

    public Location(String name, int wealth, int connectivity, Weather weather, Climate climate, List<Infrastructure> infrastructure) {
        this.name = name;
        this.wealth = wealth;
        this.connectivity = connectivity;
        this.weather = weather;
        this.logInformation = new LogInformation();
        this.publicAcceptance = new PublicAcceptance(50);
        this.climate = climate;
        this.infrastructure = infrastructure;
    }

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
