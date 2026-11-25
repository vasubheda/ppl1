package Aufgabe4;

/*
 * ImmediateAction = immediate measure that is taken to reduce a serious danger
 * (of any kind, e.g. health-threatening heat, too little drinking water, ...)
 * or its effects within a short time.
 *
 * Invariant: the measure works within a short time (hours or a few days).
 * Invariant: the effects stay short-term, they are noticeable for less than one year.
 * All assertions of Action hold here too, as it's a subtype of Action.
 */
public interface ImmediateAction extends Action {
    //
}
