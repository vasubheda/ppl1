/*
 * LocalAction is measure of any kind that affects a deterministic local area.
 *
 * Invariant: the effects of the measure stay inside this local area, and can its
 * near surroundings. The area is never the whole earth, so the measure has no worldwide effects.
 *
 * All assertions of Action hold here too as it is a subtype.
 */
public interface LocalAction extends Action {
    //
    // Postcondition: returns a text, not null, not empty, describing the local area
    // that is affected by the measure.
    // History constraint: always returns the same text.
    String area();
}
