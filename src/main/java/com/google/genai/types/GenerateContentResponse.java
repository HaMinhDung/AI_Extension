package com.google.genai.types;

public class GenerateContentResponse {
    private final String text;

    public GenerateContentResponse(String text) {
        this.text = text;
    }

    public String text() {
        return this.text;
    }
}
