package employeedb;

public enum CollabLevel {
    BAD(1, "Spatna"),
    AVERAGE(2, "Prumerna"),
    GOOD(3, "Dobra");

    private final int value;
    private final String label;

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
        if (v == 3) return GOOD;
        return null;
    }

    public static CollabLevel fromLabel(String s) {
        if (s == null) return null;
        return switch (s) {
            case "Spatna" -> BAD;
            case "Prumerna" -> AVERAGE;
            case "Dobra" -> GOOD;
            default -> null;
        };
    }
}
