package fixtures.fanin_multiple_caller;

public class CallerTwo {
    public void execute() {
        Target.compute();
    }
}