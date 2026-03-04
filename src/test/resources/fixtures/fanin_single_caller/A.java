package fixtures.fanin_single_caller;

public class A {
    public void caller() {
        B.target();
    }
}
