package com.androidth.general.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Created by jeffgan on 13/7/16.
 */
public class ProviderQuestionnaireAnswerDto {

    @JsonProperty("Id")
    public int id;

    @JsonProperty("question")
    public String question;

    @JsonProperty("answer")
    public String accountNumber;

    public ProviderQuestionnaireAnswerDto(int id, String question, String accountNumber) {
        this.id = id;
        this.question = question;
        this.accountNumber = accountNumber;
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

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderQuestionnaireAnswerDto that = (ProviderQuestionnaireAnswerDto) o;
        return id == that.id &&
                question.equals(that.question) &&
                question.equals(that.accountNumber);
    }
}
