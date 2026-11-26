package Aufgabe4;

/**
 * Part : Vishwas
 * This creates permanent green space in urban areas (trees in parks and streets, green facades, walls and roofs).
 * It cools mainly through shade and evaporation from the leaves.
 *
 * Invariant: the green space is permanent and in an urban area, CO2 binding cannot be assumed (maintenance releases CO2).
 * Invariant Inherited: warmer() == 0, consumption() > 0 (EvaporativeCooling), cooler() > 0 (Cooling),
 *  area() limited (LocalAction), duration() >= 1, annualCosts() >= 0 (PermanentAction), uses plants (PlantBased).
 * Server-History-Constraint: none.
 * Server-History-Constraint Inherited: consumption(), cooler(), warmer(), duration() constant.
 * Client-History-Constraint: none.
 */
public interface UrbanLandscaping extends PermanentAction, PlantBased, EvaporativeCooling {
}
