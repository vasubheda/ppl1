/**
 * Invariant: none
 * Invariant Inherited:
 *  effect lasts < 1 year (ImmediateAction),
 *  uses plants that evaporate water (PlantBased),
 *  warmer() == 0, consumption() > 0, cooler() > 0 (EvaporativeCooling).
 *  area() limited (LocalAction)
 * Server-History-Constraint: none
 * Server-History-Constraint Inherited:
 * consumption() constant(EvaporativeCooling).
 * Client-History-Constraint: none.
 */

public interface GreenUmbrella extends ImmediateAction, PlantBased, EvaporativeCooling {
}
