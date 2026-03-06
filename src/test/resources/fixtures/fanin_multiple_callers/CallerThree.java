package fixtures.fanin_multiple_callers;

public class CallerThree {
    public void process() {
        Target.compute();
    }
}