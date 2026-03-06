package fixtures.fanin_multiple_callers;

public class CallerOne {
    public void run() {
        Target.compute();
    }
}