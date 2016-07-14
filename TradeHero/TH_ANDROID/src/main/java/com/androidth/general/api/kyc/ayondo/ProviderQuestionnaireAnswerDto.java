package com.androidth.general.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeffgan on 13/7/16.
 */
public class ProviderQuestionnaireAnswerDto {

    @JsonProperty("Id")
    public int id;

    @JsonProperty("question")
    public String question;

    @JsonProperty("answer")
    public String answer;

    public ProviderQuestionnaireAnswerDto(@JsonProperty("answer")String answer, @JsonProperty("Id")int id, @JsonProperty("question") String question) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }
    public ProviderQuestionnaireAnswerDto(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderQuestionnaireAnswerDto that = (ProviderQuestionnaireAnswerDto) o;
        return id == that.id &&
                question.equals(that.question) &&
                question.equals(that.answer);
    }
}
