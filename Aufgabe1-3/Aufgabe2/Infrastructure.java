package Aufgabe2;

import java.util.Map;

public class Infrastructure {
    final private String name;
    private Map<String, Integer> affectedIndicators;

    public Infrastructure(String name, Map<String, Integer> affectedIndicators) {
        this.name = name;
        this.affectedIndicators = affectedIndicators;
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getAffectedIndicators() {
        return affectedIndicators;
    }
}