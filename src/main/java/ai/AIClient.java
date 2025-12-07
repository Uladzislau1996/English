package ai;

import org.springframework.stereotype.Service;

// Simplified AI client that provides offline corrections without paid APIs
@Service
public class AIClient {

    public String askAI(String userMessage) {
        String original = userMessage == null ? "" : userMessage.trim();

        if (original.isBlank()) {
            return "Please send an English sentence so I can help correct it.";
        }

        String corrected = capitalizeFirst(original);
        boolean addedCapital = !corrected.equals(original);

        if (!corrected.matches(".*[.!?]$")) {
            corrected = corrected + ".";
        }

        StringBuilder explanation = new StringBuilder("Here's what I adjusted:\n");

        if (addedCapital) {
            explanation.append("• Started the sentence with a capital letter.\n");
        }

        if (!original.matches(".*[.!?]$")) {
            explanation.append("• Added ending punctuation for completeness.\n");
        }

        if (explanation.toString().endsWith("adjusted:\n")) {
            explanation.append("• Your sentence already looked good! I kept it as is and added a friendly confirmation.\n");
        }

        return "Corrected sentence: " + corrected + "\n\n" + explanation.toString().trim();
    }

    private String capitalizeFirst(String text) {
        if (text.isBlank()) {
            return text;
        }

        char firstChar = text.charAt(0);
        if (Character.isLetter(firstChar) && Character.isLowerCase(firstChar)) {
            return Character.toUpperCase(firstChar) + text.substring(1);
        }

        return text;
    }
}
