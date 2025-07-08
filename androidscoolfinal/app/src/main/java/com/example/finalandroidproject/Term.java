package com.example.finalandroidproject;

/**
 * Represents a glossary term with a definition.
 * Used for storing and displaying educational concepts.
 */
public class Term {

    private String term;
    private String definition;

    // Transient ID is used only in-memory and not stored in Firebase
    private transient String id;

    /**
     * Empty constructor required by Firebase.
     */
    public Term() {}

    /**
     * Constructs a new Term object with the given values.
     *
     * @param term       The term or concept title
     * @param definition The definition of the term
     */
    public Term(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }

    // Getters and Setters

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

    /**
     * Returns the temporary ID used for in-memory operations (e.g. editing/deleting).
     * Not stored in Firebase.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
