package com.example.kiddoai.Config;

import java.io.UnsupportedEncodingException;

public class UnicodeDecoder {

    public static String decodeUnicode(String unicodeString) {
        StringBuilder decodedString = new StringBuilder();
        String[] unicodeChars = unicodeString.split("\\\\u");

        // The first part doesn't start with u, so add it to the decoded string.
        decodedString.append(unicodeChars[0]);

        // Loop through the rest of the unicode chars and decode them.
        for (int i = 1; i < unicodeChars.length; i++) {
            String unicodeChar = unicodeChars[i];
            int codePoint = Integer.parseInt(unicodeChar.substring(0, 4), 16); // Convert to int
            decodedString.append((char) codePoint);  // Append the decoded character
            decodedString.append(unicodeChar.substring(4)); // Append the rest of the string
        }

        return decodedString.toString();
    }

    public static void main(String[] args) {
        String transcription = "{\"text\": \"\\u0633\\u0644\\u0645\\u064A\\n\\u0645\\u062C\\u0648\\u062F\\n\"}";
        String decodedText = decodeUnicode(transcription);
        System.out.println(decodedText);  // Outputs: السلام عليكم
    }
}
