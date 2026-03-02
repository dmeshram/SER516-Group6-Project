package edu.asu.ser516.metrics;

import java.util.Objects;

public class MetricRow {
    private final String name;
    private final int value;

    public MetricRow(String name, int value) {
        this.name = Objects.requireNonNull(name, "name");
        this.value = value;
    }

    public String getName() { return name; }
    public int getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetricRow)) return false;
        MetricRow other = (MetricRow) o;
        return value == other.value && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}