/*
NOTE:
Arbeitaufteilung:
Simon: Cooling, EvaporativeCooling, Shadowing, GreenUmbrella
Vishwas: CO2Binding, Forestation, PlantBased, UrbanLandscaping
Jay: Action, LocalAction, PermanentAction, ImmediateAction, WaterSupply

Simon Hahn Use of Ai:
        #TODO @Simon Hahn

Vishwas Vikram Bheda Use of Ai:
        #TODO @Vishwas Vikram Bheda

Dhananjay Sharma Use of Ai:
        #TODO @dhananjaysh18
*/

/*
    Super- and Sub-Types:
        Action: no Sub Type is the "Beginning"
        Local Action: Sub-Type of: Action, Cause: #TODO @dhananjaysh18
        Permanent Action: Sub-Type of: Action; Cause: #TODO @dhananjaysh18
        Immediate Action: Sub-Type of: Action; Cause: #TODO @dhananjaysh18
        WaterSupply: Sub-Type of: Local Action; Cause: #TODO @dhananjaysh18
                     No Sub-Type of: Permanent Action, Immediate Action; Cause: #TODO @dhananjaysh18 (The instructions specifically state that it makes no difference how long it takes to take effect therefore, both are possible, but neither is certain.)
        Shadowing: Sub-Type of: Local Action, Permanent Action, Cooling; Cause: it is specified that it's a long-therm-measure therefor it can replace a long therm measure,
                   it's installed as local infrastructure, it's colling a specific local place that makes it cooling
                   No Sub-Type of: Immediate Action; cause: The measure is designed to have a long-term impact therefor it can't be a Immediate measure
        Cooling: Sub-Type of: Local Action; Cause: its a measure to cool a local place with air-conditioning or other types like water vaporisation.
                 No Sub-Type of: Permanent Action, Immediate Action; Cause: It cannot be categorized as immediate or lon-therm it can be both for example building air conditioning (>1 year) or get plants.
        EvaporativeCooling: Sub-Type of: Cooling; Cause: every type of evaporization-cooling is cooling specified in cooling
        GreenUmbrella: Sub-Type of: Local Action, Immediate Action, PlantBased, EvaporativeCooling; Cause: an umbrella cannot influence more than what is under him.
                       An Green-Umbrella is a advertisement campaign specifically only for a shorth-therm constructed(also why no Permanent Action).
                       it's a measure that, among other things, uses plants to cool down threw the vaporisation
                       It works among others through vaporisation which makes it EvaporativeCooling
        CO2Binding: Sub-Type of: Permanent Action; Cause: #TODO @Vishwas Vikram Bheda
                    No Sub-Type of: Local Action, Immediate Action; Cause: #TODO @Vishwas Vikram Bheda (because global and co2 has no immediate effect)
        Forestation: Sub-Type of: Permanent Action, CO2Binding, PlantBased; Cause: #TODO @Vishwas Vikram Bheda
                     No Sub-Type of: Local Action, Immediate Action; Cause: #TODO @Vishwas Vikram Bheda(The description mentions both global and local effects)
        PlantBased: Sub-Type of: Action; Cause: #TODO @Vishwas Vikram Bheda
                    No Sub-Type of: Local-Action, Permanent Action, Immediate Action, EvaporativeCooling; Cause: #TODO @Vishwas Vikram Bheda (EvaporativeCooling-keine Ahnung)
        UrbanLandscaping: Sub-Type of: Local-Action, Permanent Action, PlantBased, EvaporativeCooling; Cause: #TODO @Vishwas Vikram Bheda
                          No Sub-Type of: Immediate Action, shadowing; Cause: #TODO @Vishwas Vikram Bheda(shadowing is done where humans are often landscaping can be done outside on the roadway where humans are note preferred visitors)
*/

public class Test4 {
}
