/**
 * Part : Vishwas
 * This is a measure that uses plants of any kind, short-lived (cut flowers) or long-lived (trees).
 *
 * Invariant: the measure uses plants, and the plants evaporate water through their surface.
 * Invariant: nothing is fixed about how long the effect lasts or how big the area is.
 * Invariant Inherited: costs() >= 0 (Action).
 * Server-History-Constraint: none.
 * Server-History-Constraint Inherited: precondition(), expectations(), costs() constant.
 * Client-History-Constraint: none.
 */
public interface PlantBased extends Action {
}
