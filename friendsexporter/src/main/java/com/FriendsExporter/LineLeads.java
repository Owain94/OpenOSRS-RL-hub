package com.FriendsExporter;

public enum LineLeads {
    None("None"),
    Number("[Number]"),
    Number1("[Number]."),
    Number2("[Number])"),
    Number3("[Number].)")
    ;

    private final String name;

    public String toString() {
        return this.getName();
    }

    public String getName() {
        return this.name;
    }

    private LineLeads(String name) {
        this.name = name;
    }
}
