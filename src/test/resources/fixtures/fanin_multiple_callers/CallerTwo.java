package fixtures.fanin_multiple_callers;

public class CallerTwo {
    public void execute() {
        Target.compute();
    }
}