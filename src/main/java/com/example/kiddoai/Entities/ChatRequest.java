package com.example.kiddoai.Entities;

public class ChatRequest {
    private String threadId;
    private String userInput;

    // Getters and setters
    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}