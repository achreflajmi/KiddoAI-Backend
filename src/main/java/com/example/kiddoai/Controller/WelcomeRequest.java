package com.example.kiddoai.Controller;

public class WelcomeRequest {
    private String threadId;
    private String IQCategory; // Changed from niveauIQ to IQCategory

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getIQCategory() {
        return IQCategory;
    }

    public void setIQCategory(String IQCategory) {
        this.IQCategory = IQCategory;
    }
}

