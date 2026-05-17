package com.assignment.model;

public enum BillType {
    ELECTRIC,
    WATER,
    INTERNET,
    OTHER;

    public static BillType fromToken(String token) {
        if (token == null) return OTHER;
        String t = token.trim().toUpperCase();
        if (t.isEmpty()) return OTHER;

        switch (t) {
            case "ELECTRIC":
                return ELECTRIC;
            case "WATER":
                return WATER;
            case "INTERNET":
                return INTERNET;
            default:
                return OTHER;
        }
    }
}
