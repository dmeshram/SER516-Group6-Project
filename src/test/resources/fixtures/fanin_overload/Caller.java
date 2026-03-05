package fixtures.fanin_overload;

public class Caller {
    public void callInt() {
        Over.f(1);
    }
    public void callString() {
        Over.f("x");
    }
}