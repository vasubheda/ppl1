package Aufgabe4;

/*
 * Action is any measure that is taken
 * All measures form a doubly linked list ordered by time:
 * previous() is the measure taken directly before,
 * next() the one taken directly after
 *
 * Invariant: if next() != null then next().previous() == this
 * Invariant: if previous() != null then previous().next() == this
 * Invariant: list has no cycles, so a measure is at most once in the list.
 */

public interface Action {


    // Postcondition: returns a short text (not null, not empty) that describes the
    // preconditions which must be met so the measure can be taken.
    // History constraint: always returns the same text
    String precondition();

    // Postcondition: returns a short text (not null, not empty) that describes how
    // the measure works and which effects are expected.
    // History constraint: always returns the same text.
    String expectations();

    // Postcondition: returns the approx. costs in the year the measure is taken and >= 0.
    // History constraint: the value does not change.
    double costs();

    // Postcondition: returns the measure taken directly before it, or null if
    //  there is none. The list is not changed.
    Action previous();

    // Postcondition: returns the measure taken directly after this, or null if
    // there is none. The list is not changed.
    Action next();

    // Precondition: action != null, action != this, and action is not in a list yet
    // Postcondition: action is inserted directly after this. The measure that was after
    // this before (if any) is now after action. Nothing else in the list changes.
    void add(Action action);

    // Precondition: action != null, action != this, and action is not in a list yet
    // Postcondition: action is inserted directly before this. The measure that was before
    // this before (if any) is now before action. Nothing else in the list changes.
    void addBefore(Action action);

    // Postcondition: this is removed from the list. The measures before and after this,
    // if any, are now directly next to each other. this has no previous or next anymore.
    void remove();

}
