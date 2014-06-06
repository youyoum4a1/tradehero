package com.tradehero.th.api.translation.bing;

import com.tradehero.th.R;
import com.tradehero.th.api.translation.TranslationResult;
import org.simpleframework.xml.Text;

public class BingTranslationResult extends TranslationResult
{
    private String fromLanguageCode;
    private String languageCode;

    @Text()
    private String content;

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

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    @Override public int logoResId()
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
