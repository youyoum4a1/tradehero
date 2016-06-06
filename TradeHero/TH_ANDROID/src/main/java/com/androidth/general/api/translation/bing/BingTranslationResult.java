package com.androidth.general.api.translation.bing;

import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.api.translation.TranslationResult;

public class BingTranslationResult extends TranslationResult
{
    private static final String PATTERN_FROM = "\\]\\s+\\([Tt]radehero://security/";
    private static final String PATTERN_TO = "](tradehero://security/";

    private String fromLanguageCode;
    private String languageCode;

    private String content;

    @SuppressWarnings("UnusedDeclaration") BingTranslationResult()
    {
    }

    public BingTranslationResult(
            String fromLanguageCode,
            String languageCode,
            String content)
    {
        this.fromLanguageCode = fromLanguageCode;
        this.languageCode = languageCode;
        this.content = content;
    }

    public String getFromLanguageCode()
    {
        return fromLanguageCode;
    }

    public void setFromLanguageCode(String fromLanguageCode)
    {
        this.fromLanguageCode = fromLanguageCode;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    @Nullable public String getContent()
    {
        if (content != null)
        {
            content = content.replaceAll(PATTERN_FROM, PATTERN_TO);
        }

        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    @Override @DrawableRes public int logoResId()
    {
        return R.drawable.logo_bing;
    }

    @Override
    public String toString()
    {
        return "BingTranslationResult{" +
                "fromLanguageCode='" + fromLanguageCode + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
