package com.tradehero.th.models.translation;

import org.simpleframework.xml.Text;

/**
 * Created by tradehero on 14-3-5.
 */
public class TranslationResult {

    private String fromLanguageCode;

    private String languageCode;

    @Text()
    private String content;

    public String getFromLanguageCode() {
        return fromLanguageCode;
    }

    public void setFromLanguageCode(String fromLanguageCode) {
        this.fromLanguageCode = fromLanguageCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TranslationResult{" +
                "fromLanguageCode='" + fromLanguageCode + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
