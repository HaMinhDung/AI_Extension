package com.extension.AITranslatorExtension.dto;

public class TranslateResponse {

    private String result;
    private boolean success;
    private String error;
    private boolean fromCache;

    public TranslateResponse() {
    }

    public TranslateResponse(String result, boolean success) {
        this.result = result;
        this.success = success;
        this.fromCache = false;
    }

    public TranslateResponse(String result, boolean success, boolean fromCache) {
        this.result = result;
        this.success = success;
        this.fromCache = fromCache;
    }

    public TranslateResponse(String result, boolean success, String error) {
        this.result = result;
        this.success = success;
        this.error = error;
        this.fromCache = false;
    }

    public static TranslateResponse success(String result) {
        return new TranslateResponse(result, true, false);
    }

    public static TranslateResponse success(String result, boolean fromCache) {
        return new TranslateResponse(result, true, fromCache);
    }

    public static TranslateResponse error(String error) {
        TranslateResponse response = new TranslateResponse();
        response.setSuccess(false);
        response.setError(error);
        response.setFromCache(false);
        return response;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }
}
