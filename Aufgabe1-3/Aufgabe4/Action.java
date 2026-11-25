package Aufgabe4;

public interface Action {
    // #TODO: Assertions
    String precondition();
    String expectations();
    double costs();
    Action previous();
    Action next();
    void add(Action action);
    void addBefore(Action action);
    void remove();
}
