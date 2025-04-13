package com.example.finalandroidproject;

public class Term {
    private String term;
    private String definition;

    private transient String id; // ה-ID נשמר זמנית בזיכרון ולא נשמר בפיירבייס

    // בנאי ריק (נדרש עבור Firebase)
    public Term() {}

    // בנאי מלא
    public Term(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    // Getters ו-Setters
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
