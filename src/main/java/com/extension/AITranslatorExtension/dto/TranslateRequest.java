package com.extension.AITranslatorExtension.dto;

import jakarta.validation.constraints.NotBlank;

public class TranslateRequest {

    @NotBlank(message = "Text is required")
    private String text;

    @NotBlank(message = "Prompt is required")
    private String prompt;

    public TranslateRequest() {
    }

    public TranslateRequest(String text, String prompt) {
        this.text = text;
        this.prompt = prompt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
