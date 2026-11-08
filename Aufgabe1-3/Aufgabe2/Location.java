package Aufgabe2;

import java.util.List;

public class Location {
    private String name;
    private Weather weather;
    private LogInformation logInformation;
    private Climate climate;
    private PublicAcceptance publicAcceptance;
    private List<Infrastructure> infrastructure;

    public Location(String name, Weather weather, Climate climate, List<Infrastructure> infrastructure) {
        this.name = name;
        this.weather = weather;
        this.logInformation = new LogInformation();
        this.publicAcceptance = new PublicAcceptance(50);
        this.climate = climate;
        this.infrastructure = infrastructure;
    }




}
