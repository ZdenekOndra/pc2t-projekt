package employeedb;

public enum CollabLevel {
    BAD(1, "Spatna"),
    AVERAGE(2, "Prumerna"),
    GOOD(3, "Dobra");

    private int value;
    private String label;

    CollabLevel(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static CollabLevel fromValue(int v) {
        if (v == 1) return BAD;
        if (v == 2) return AVERAGE;
        return GOOD;
    }

    public static CollabLevel fromLabel(String s) {
        if (s.equals("Spatna")) return BAD;
        if (s.equals("Prumerna")) return AVERAGE;
        return GOOD;
    }
}
