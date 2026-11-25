import Aufgabe4.Action;
import Aufgabe4.LocalAction;
import Aufgabe4.WaterSupply;
import Aufgabe4.PermanentAction;
import Aufgabe4.ImmediateAction;

/*
NOTE:
Arbeitaufteilung:
Simon: Cooling, EvaporativeCooling, Shadowing, GreenUmbrella
Vishwas: CO2Binding, Forestation, PlantBased, UrbanLandscaping
Dhananjay: Action, LocalAction, PermanentAction, ImmediateAction, WaterSupply

Simon Hahn Use of Ai:
        #TODO @Simon Hahn

Vishwas Vikram Bheda Use of Ai:
        #TODO @Vishwas Vikram Bheda

Dhananjay Sharma Use of Ai: Used Claude
        https://claude.ai/share/cb03bf00-e2b3-4e8b-b560-54e3498803b9
*/

/*
    Super- and Sub-Types:
        Action: no Sub Type is the "Beginning"
        Local Action: Sub-Type of: Action, Cause: Everything that holds for Action holds for it too, only with the extra property that its effect is limited to a determinable local area.
                      No Sub-Type of: Permanent Action, Immediate Action; Cause: Local Action is about space, Permanent and Immediate Action are about time.
        Permanent Action: Sub-Type of: Action; Cause: Everything that holds for Action holds for it too, only with the extra property that its effect is noticeable for about one year or longer.
                          No Sub-Type of: Immediate Action; Cause: a Permanent Action is noticeable for one year or longer, the effects of an Immediate Action last less than one year.
        Immediate Action: Sub-Type of: Action; Cause: Everything that holds for Action holds for it too, only with the extra property that it reduces a serious danger within a short time and its effects last less than one year.
        WaterSupply: Sub-Type of: Local Action; Cause: Everything that holds for Local Action holds for it too, only with the extra property that it improves or secures the drinking-water supply of that area.
                     No Sub-Type of: Permanent Action, Immediate Action; Cause: how fast it takes effect doesn't matter and how long the effect lasts is not given, so it can be short or long.
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

    public static void main(String[] args) {
        testWaterSupply();
        // TODO Simon, Vishwas
    }

    // ===== Tests: Action, LocalAction, PermanentAction, ImmediateAction, WaterSupply =====
    // subtype objects are used over supertype references, supertype assertions must still hold
    private static void testWaterSupply() {
        TestWater pipe = new TestWater("ground can be dug", "village gets more water", 400000, "village Trofaiach", 20000);
        TestWater tank = new TestWater("roof is big enough", "rain water is collected", 1500, "one house", 3);
        TestPermanent law = new TestPermanent("parliament agrees", "less fossil fuel is used", 1000000, 15, 200000);
        TestImmediate heat = new TestImmediate("heat warning", "cooled rooms are opened", 5000);

        for (Action a : new Action[]{pipe, tank, law, heat}) {
            System.out.println("Action: " + a.precondition() + " | " + a.costs() + " -> "
                    + (!a.precondition().isEmpty() && !a.expectations().isEmpty() && a.costs() >= 0));
        }
        LocalAction l = tank;
        System.out.println("LocalAction area: " + l.area() + " -> " + !l.area().isEmpty());
        WaterSupply w = tank;
        System.out.println("WaterSupply capacity: " + w.capacity() + " -> " + (w.capacity() > 0));
        PermanentAction p = law;
        System.out.println("PermanentAction: " + p.duration() + " years, " + p.annualCosts() + " -> "
                + (p.duration() >= 1 && p.annualCosts() >= 0));

        heat.add(law);         // heat, law
        law.add(pipe);         // heat, law, pipe
        pipe.addBefore(tank);  // heat, law, tank, pipe
        System.out.println("list forward: " + (heat.next() == law && law.next() == tank && tank.next() == pipe));
        System.out.println("list backward: " + (pipe.previous() == tank && tank.previous() == law && law.previous() == heat));
        law.remove();          // heat, tank, pipe
        System.out.println("after remove: " + (heat.next() == tank && tank.previous() == heat
                && law.previous() == null && law.next() == null));
    }

    // ===== classes below are ONLY for testing, NOT part of the system =====

    // base class for all test classes, implements doubly linked list of Action(previous and next)
    private static class TestAction implements Action {
        private final String pre, exp;
        private final double costs;
        private TestAction prev, next;

        TestAction(String pre, String exp, double costs) { this.pre = pre; this.exp = exp; this.costs = costs; }

        public String precondition() { return pre; }
        public String expectations() { return exp; }
        public double costs() { return costs; }
        public Action previous() { return prev; }
        public Action next() { return next; }

        public void add(Action action) {
            TestAction a = (TestAction) action;
            a.prev = this;
            a.next = next;
            if (next != null) next.prev = a;
            next = a;
        }

        public void addBefore(Action action) {
            TestAction a = (TestAction) action;
            a.next = this;
            a.prev = prev;
            if (prev != null) prev.next = a;
            prev = a;
        }

        public void remove() {
            if (prev != null) prev.next = next;
            if (next != null) next.prev = prev;
            prev = null;
            next = null;
        }
    }

    private static class TestPermanent extends TestAction implements PermanentAction {
        private final int duration;
        private final double annual;
        TestPermanent(String pre, String exp, double costs, int duration, double annual) {
            super(pre, exp, costs); this.duration = duration; this.annual = annual;
        }
        public int duration() { return duration; }
        public double annualCosts() { return annual; }
    }

    private static class TestImmediate extends TestAction implements ImmediateAction {
        TestImmediate(String pre, String exp, double costs) { super(pre, exp, costs); }
    }

    private static class TestWater extends TestAction implements WaterSupply {
        private final String area;
        private final double capacity;
        TestWater(String pre, String exp, double costs, String area, double capacity) {
            super(pre, exp, costs); this.area = area; this.capacity = capacity;
        }
        public String area() { return area; }
        public double capacity() { return capacity; }
    }
}