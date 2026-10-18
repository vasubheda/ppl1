// Splits the yearly budget into immediate, cause and shortfall, all values are in percentage of the yearly budget.
public final class YearBudget{
    private final double immediate;
    private final double cause;
    private final double shortfall;

    public YearBudget(double immediate, double cause, double shortfall){
        this.immediate = immediate;
        this.cause=cause;
        this.shortfall = shortfall;
    }

    public double getImmediate(){
        return immediate;
    }
    public double getCause(){
        return cause;
    }
    public double getShortfall(){
        return shortfall;
    }
}
