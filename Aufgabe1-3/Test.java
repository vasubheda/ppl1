/*
NOTE:
Arbeitaufteilung:
Simon: Cooling, EvaporativeCooling, Shadowing, GreenUmbrella
Vishwas: CO2Binding, Forestation, PlantBased, UrbanLandscaping
Jay: Action, LocalAction, PermanentAction, ImmediateAction, WaterSupply

Simon Hahn Use of Ai:
    Used Claude to Control the result: https://claude.ai/share/c8c91ee2-8520-4ef2-a6e6-27461742e818

Vishwas Vikram Bheda Use of Ai:
        #TODO @Vishwas Vikram Bheda

Dhananjay Sharma Use of Ai: Used Claude:
        https://claude.ai/share/cb03bf00-e2b3-4e8b-b560-54e3498803b9
*/

/*
    Super- and Sub-Types:
        Action: no Sub Type is the "Beginning"
        Local Action: Sub-Type of: Action, Cause: Everything that holds for Action holds for it too, only with the extra property that its effect is limited to a determinable local area.
        Permanent Action: Sub-Type of: Action; Cause: Everything that holds for Action holds for it too, only with the extra property that its effect is noticeable for about one year or longer.
        Immediate Action: Sub-Type of: Action; Cause: Everything that holds for Action holds for it too, only with the extra property that it reduces a serious danger within a short time and its effects last less than one year.
        WaterSupply: Sub-Type of: Local Action; Cause: Everything that holds for Local Action holds for it too, only with the extra property that it improves or secures the drinking-water supply of that area.
        Shadowing: Sub-Type of: Local Action, Permanent Action, Cooling; Cause: it is specified that it's a long-therm-measure therefor it can replace a long therm measure,
                   it's installed as local infrastructure, it's colling a specific local place that makes it cooling
        Cooling: Sub-Type of: Local Action; Cause: its a measure to cool a local place with air-conditioning or other types like water vaporisation.
        EvaporativeCooling: Sub-Type of: Cooling; Cause: every type of evaporization-cooling is cooling specified in cooling
        GreenUmbrella: Sub-Type of: Local Action, Immediate Action, PlantBased, EvaporativeCooling; Cause: an umbrella cannot influence more than what is under him.
                       An Green-Umbrella is a advertisement campaign specifically only for a shorth-therm constructed(also why no Permanent Action).
                       it's a measure that, among other things, uses plants to cool down threw the vaporisation
                       It works among others through vaporisation which makes it EvaporativeCooling
        CO2Binding: Sub-Type of: Permanent Action; Cause: #TODO @Vishwas Vikram Bheda
        Forestation: Sub-Type of: Permanent Action, CO2Binding, PlantBased; Cause: #TODO @Vishwas Vikram Bheda
        PlantBased: Sub-Type of: Action; Cause: #TODO @Vishwas Vikram Bheda
        UrbanLandscaping: Sub-Type of: Local-Action, Permanent Action, PlantBased, EvaporativeCooling; Cause: #TODO @Vishwas Vikram Bheda

    All-other No Sub-Type of Super- and Sub-Types Combinations:
        Local Action, Plant Based, Cooling, EvaporativeCooling, WaterSupply, No Sub-Type of: Permanent Action, Immediate Action;
            Cause: each of these can be short-term OR long-term, so none of them is always short-term or always long-term
        CO2Binding, Shadowing, Forestation, UrbanLandscaping No Sub-Type of: ImmediateAction;
            Cause: they are long-term by definition (>= 1 year), so their effect cannot be shorter than a year
        PermanentAction No Sub-Type of: ImmediateAction;
            Cause: short-term (< 1 year) and long-term (>= 1 year) exclude each other
        GreenUmbrella No Sub-Type of: PermanentAction, UrbanLandscaping;
            Cause: explicitly not a permanent measure; UrbanLandscaping is permanent which means they exclude each other
        CO2Binding, Forestation No Sub-Type of: LocalAction, Cooling, EvaporativeCooling, WaterSupply, Shadowing, UrbanLandscaping, GreenUmbrella
            Cause: CO2Binding/Forestation act worldwide, the others act only in a determinable local area. A worldwide effect is not a local-only effect and vice versa.
        Same Rough Cause for all following: a measure of one kind does not guarantee another measure's effects
            PlantBased No Sub-Type of: LocalAction, Cooling, EvaporativeCooling, WaterSupply, CO2Binding, Shadowing
                Cause: using plants guarantees neither locality (Forestation is global), nor cooling (plants can be pure decoration), nor drinking water, nor long-term CO2 binding and none of these needs plants
            Cooling, EvaporativeCooling No Sub-Type of: WaterSupply
                Cause: lowering temperature is not securing drinking water
            EvaporativeCooling No Sub-Type of: Shadowing
                Cause: both cool but by different means (water evaporation vs blocking the sun)
            WaterSupply No Sub-Type of: Shadowing, UrbanLandscaping, GreenUmbrella
                Cause: securing water is neither shading nor greening
            Shadowing No Sub-Type of: UrbanLandscaping, GreenUmbrella
                Cause: shadowing may be purely technical (no plants). not every urban greening shades a human centered space
            GreenUmbrella No Sub-Type of: Shadowing
                Cause: a GreenUmbrella provides shade, but Shadowing is a long-term measure that explicitly excludes temporarily placed sun-umbrellas or tents
            Shadowing No Sub-Type of: EvaporativeCooling
                Cause: Shadowing cools by blocking the sun, not by water evaporation, so it has no consumption()
*/

import Aufgabe4.*;



public class Test {
    public static void main(String[] args) {
        testWaterSupply();
        testSimon();
        // TODO Vishwas
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

    // ===== Tests: Cooling, EvaporativeCooling, Shadowing, GreenUmbrella (Simon) =====
    // each subtype object is used over a supertype reference, the supertype's assertions must still hold
    private static void testSimon() {
        TestCooling ac = new TestCooling("power available", "room is cooled", 3000, "server room", 6.0, 2.0);
        TestEvaporativeCooling mist = new TestEvaporativeCooling("water available", "air cooled by mist", 800, "terrace", 4.0, 0.0, 25.0);
        TestShadowing sail = new TestShadowing("mounting point", "shaded and cooler", 2000, "courtyard", 3.0, 0.0, 20, 100, 0.7);
        TestGreenUmbrella umbrella = new TestGreenUmbrella("heat warning", "shade + evaporation", 150, "under the umbrella", 1.5, 0.0, 2.0);

        // Cooling invariant (cooler>0, warmer>=0) must hold for EVERY Cooling subtype
        for (Cooling c : new Cooling[]{ac, mist, sail, umbrella}) {
            System.out.println("Cooling: cooler=" + c.cooler() + " warmer=" + c.warmer()
                    + " -> " + (c.cooler() > 0 && c.warmer() >= 0));
        }
        // EvaporativeCooling invariant (warmer==0, consumption>0) -> strengthens Cooling
        for (EvaporativeCooling e : new EvaporativeCooling[]{mist, umbrella}) {
            System.out.println("EvaporativeCooling: warmer=" + e.warmer() + " consumption=" + e.consumption()
                    + " -> " + (e.warmer() == 0.0 && e.consumption() > 0));
        }
        // Shadowing used over its supertypes
        LocalAction sl = sail;
        System.out.println("Shadowing as LocalAction: " + sl.area() + " -> " + !sl.area().isEmpty());
        PermanentAction sp = sail;
        System.out.println("Shadowing as PermanentAction: " + sp.duration() + "y -> " + (sp.duration() >= 1 && sp.annualCosts() >= 0));
        System.out.println("Shadowing transparency: " + sail.transparency() + " -> " + (sail.transparency() > 0 && sail.transparency() <= 1));
        // GreenUmbrella used over LocalAction
        LocalAction gl = umbrella;
        System.out.println("GreenUmbrella as LocalAction: " + gl.area() + " -> " + !gl.area().isEmpty());

        // VIOLATION demo: An object with `cooler() == 0` is not a valid shadowing, because it violates cooling Invariant -> must print false
        TestShadowing broken = new TestShadowing("x", "y", 1, "spot", 0.0, 0.0, 5, 10, 0.5);
        Cooling bc = broken;
        System.out.println("VIOLATION Shadowing-as-Cooling (cooler=0): " + (bc.cooler() > 0 && bc.warmer() >= 0) + "  (expected false)");
    }

    // ===== classes below are ONLY for testing =====

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

    private static class TestCooling extends TestAction implements Cooling {
        private final String area; private final double cooler, warmer;
        TestCooling(String pre, String exp, double costs, String area, double cooler, double warmer) {
            super(pre, exp, costs); this.area = area; this.cooler = cooler; this.warmer = warmer;
        }
        public String area() { return area; }
        public double cooler() { return cooler; }
        public double warmer() { return warmer; }
    }

    private static class TestEvaporativeCooling extends TestAction implements EvaporativeCooling {
        private final String area; private final double cooler, warmer, consumption;
        TestEvaporativeCooling(String pre, String exp, double costs, String area, double cooler, double warmer, double consumption) {
            super(pre, exp, costs); this.area = area; this.cooler = cooler; this.warmer = warmer; this.consumption = consumption;
        }
        public String area() { return area; }
        public double cooler() { return cooler; }
        public double warmer() { return warmer; }
        public double consumption() { return consumption; }
    }

    private static class TestShadowing extends TestAction implements Shadowing {
        private final String area; private final double cooler, warmer; private final int duration;
        private final double annual, transparency;
        TestShadowing(String pre, String exp, double costs, String area, double cooler, double warmer, int duration, double annual, double transparency) {
            super(pre, exp, costs); this.area = area; this.cooler = cooler; this.warmer = warmer;
            this.duration = duration; this.annual = annual; this.transparency = transparency;
        }
        public String area() { return area; }
        public double cooler() { return cooler; }
        public double warmer() { return warmer; }
        public int duration() { return duration; }
        public double annualCosts() { return annual; }
        public double transparency() { return transparency; }
    }

    private static class TestGreenUmbrella extends TestAction implements GreenUmbrella {
        private final String area; private final double cooler, warmer, consumption;
        TestGreenUmbrella(String pre, String exp, double costs, String area, double cooler, double warmer, double consumption) {
            super(pre, exp, costs); this.area = area; this.cooler = cooler; this.warmer = warmer; this.consumption = consumption;
        }
        public String area() { return area; }
        public double cooler() { return cooler; }
        public double warmer() { return warmer; }
        public double consumption() { return consumption; }
    }
}
