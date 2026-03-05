package fixtures.fanin_multiple_caller;

public class CallerThree {
    public void process() {
        Target.compute();
    }
}