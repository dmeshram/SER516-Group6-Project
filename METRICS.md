# Metric Definitions

## Class-Level Fan-Out

**Definition**: The number of **unique** classes (within the project) that a specific class references. High fan-out suggests a class has many dependencies and may be brittle.

### Computation Rules

1.  **Unit of Analysis**:
    *   Top-level `class`, `interface`, or `enum`.
    *   Inner/nested classes are treated as part of the containing top-level class. Dependencies *within* the same file (e.g., inner class referencing outer class) do not count.

2.  **Dependencies (What counts)**:
    A class `A` has a dependency on class `B` if `A` explicitly references `B` in any of the following ways:
    *   **Inheritance**: `extends B` or `implements B`.
    *   **Fields**: Declaring a member variable of type `B` (e.g., `private B myField;`).
    *   **Methods**:
        *   Method return type is `B`.
        *   Method parameter type is `B`.
        *   Method throws exception `B`.
    *   **Local Variables**: Declaring a variable of type `B` inside a method.
    *   **Instantiations**: Calling `new B()`.
    *   **Static Access**: Accessing `B.staticMethod()` or `B.STATIC_FIELD`.
    *   **Type Casting**: Casting an object to type `B`.
    *   **Annotations**: Using `@B` (if `B` is a custom annotation defined in the project).
    *   **Generics**: Using `B` as a type argument (e.g., `List<B>`).

3.  **Exclusions (What to ignore)**:
    *   **Standard Library**: All types under `java.*`, `javax.*`, `jdk.*`, `sun.*` are **ignored**. We are measuring *internal* project coupling.
    *   **Primitives**: `int`, `boolean`, `char`, `void`, etc., are **ignored**.
    *   **Self-Reference**: A class referring to itself is **ignored**.
    *   **Third-Party Libraries**: References to external libraries (e.g., `org.junit.*`, `com.google.*`) are **ignored** unless explicitly stated otherwise. Only classes defined *within the project source* count.

4.  **Counting**:
    *   The metric is a count of **unique** types.
    *   *Example*: If class `A` has two fields of type `B` and one method returning `B`, the Fan-Out contribution from `B` is **1**.

## Class-Level Fan-In

**Definition**: The number of other classes (within the project) that reference the target class. High fan-in suggests a class is a common utility or core component; changing it may break many other classes.

*Derived strictly by reversing the Fan-Out relationships defined above.*

### Computation Rules

**Unit of Analysis:**
- A Java method or constructor declared in `.java` files under the configured input path.

**Function Identity (Canonical ID):**
- `<package>.<Class>#<method>(<paramTypes>)`
- Constructors use: `<package>.<Class>#<init>(<paramTypes>)`

**Dependencies (What counts):**
A function `Caller` contributes to Fan-In of function `Callee` if `Caller` contains:
- A direct or instance method call to `Callee`
- A static method call to `Callee`
- A constructor invocation `new ClassName(...)` (counts as a call to `<init>`)

**Exclusions (What to ignore):**
- Self-call: `Caller == Callee` does not count.
- Duplicate calls: Multiple calls from the same `Caller` to the same `Callee` count once (distinct caller rule).
- External library/JDK calls (e.g., `java.*`, `javax.*`, `jdk.*`, `sun.*`) are ignored.
- Unresolved calls (calls that cannot be resolved to a method declared within the scanned project source) are ignored.

**Counting:**
- Fan-In(Callee) = number of **distinct caller functions** that call `Callee` at least once.
- Example: If `A#a()` calls `B#b()` twice and `C#c()` calls `B#b()` once, then Fan-In(`B#b()`) = 2.

### Examples

```java
class A { void a(){ B.b(); } }
class B { static void b(){} }