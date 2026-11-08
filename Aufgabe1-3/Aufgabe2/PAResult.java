import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Part: Dhananjay Sharma.
 *
 * The result of one monthly update of the public acceptance: the new PA value, how much it changed and which
 * indicators caused the drop.
 * The cause names are the names of SimConstants.INDICATOR_NAMES, so TeamAdapter can translate them back
 * into indicator indexes.
 *
 * Values are set once in the constructor and can only be read with getters,
 * nobody can change the result afterwards (data hiding).
 */
public class PAResult {
    private final double pa;
    private final double delta;
    private final List<String> causes;

    public PAResult(double pa, double delta, List<String> causes) {
        this.pa = pa;
        this.delta = delta;
        this.causes = Collections.unmodifiableList(new ArrayList<>(causes));
    }

    /** Public Acceptance after the update (0 to 100) */
    public double getPa() {
        return pa;
    }

    /** change of the public acceptance in this update, negative = got lower */
    public double getDelta() {
        return delta;
    }

    /** names of the indicators whose threshold was violated, empty if none */
    public List<String> getCauses() {
        return causes;
    }
}
