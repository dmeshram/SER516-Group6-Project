package fixtures.fanin_multiple_caller;

public class CallerOne {
    public void run() {
        Target.compute();
    }
}