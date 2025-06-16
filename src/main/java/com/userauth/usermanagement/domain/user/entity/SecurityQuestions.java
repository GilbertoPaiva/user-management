package com.userauth.usermanagement.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityQuestions {
    private String question1;
    private String answer1;
    private String question2;
    private String answer2;
    private String question3;
    private String answer3;

    public boolean validateAnswer(String question, String answer) {
        if (question1 != null && question1.equals(question)) {
            return answer1 != null && answer1.equals(answer);
        }
        if (question2 != null && question2.equals(question)) {
            return answer2 != null && answer2.equals(answer);
        }
        if (question3 != null && question3.equals(question)) {
            return answer3 != null && answer3.equals(answer);
        }
        return false;
    }

    public String getRandomQuestion() {
        if (question1 != null) return question1;
        if (question2 != null) return question2;
        if (question3 != null) return question3;
        return null;
    }
}
