package com.androidth.general.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by jeffgan on 13/7/16.
 */
public class ProviderQuestionnaireDTO {

    @JsonProperty("Id")
    public int id;

    @JsonProperty("question")
    public String question;

    @JsonProperty("controlType")
    public String controlType;

    @JsonProperty("format")
    public String format;

    @JsonProperty("values")
    public String values;

    @JsonProperty("isRequired")
    public boolean isRequired;

    @JsonProperty("validationRegex")
    public String validationRegex;

    @JsonProperty("pageNumber")
    public int pageNumber;

    @JsonProperty("sortOrder")
    public int sortOrder;

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

    public String getControlType() {
        return controlType;
    }

    public String getFormat() {
        return format;
    }

    public String getValues() {
        return values;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getValidationRegex() {
        return validationRegex;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderQuestionnaireDTO that = (ProviderQuestionnaireDTO) o;
        return getId() == that.getId() &&
                isRequired() == that.isRequired() &&
                getPageNumber() == that.getPageNumber() &&
                getSortOrder() == that.getSortOrder() &&
                getQuestion().equals(that.getQuestion()) &&
                getControlType().equals(that.getControlType()) &&
                getFormat().equals(that.getFormat()) &&
                getValues().equals(that.getValues()) &&
                getValidationRegex().equals(that.getValidationRegex());
    }
}
