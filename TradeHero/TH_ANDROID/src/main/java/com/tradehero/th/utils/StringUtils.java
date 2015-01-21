package com.tradehero.th.utils;

import android.text.TextUtils;
import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
    public static String join(String glue, List elements)
    {
        if (elements == null)
        {
            return null;
        }
        int length = elements.size();
        if (length == 0)
        {
            return null;
        }

        StringBuilder out = new StringBuilder();
        out.append(elements.get(0));
        for (int x = 1; x < length; ++x)
        {
            out.append(glue).append(elements.get(x));
        }
        return out.toString();
    }

    public static String join(String glue, Object... elements)
    {
        if (elements == null)
        {
            return null;
        }
        int length = elements.length;
        if (length == 0)
        {
            return null;
        }

        StringBuilder out = new StringBuilder();
        out.append(elements[0]);
        for (int x = 1; x < length; ++x)
        {
            out.append(glue).append(elements[x]);
        }
        return out.toString();
    }

    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }

    public static boolean isNullOrEmptyOrSpaces(String str)
    {
        return str == null || str.trim().isEmpty();
    }

    public static boolean containSpecialChars(String inputStr){
        if(TextUtils.isEmpty(inputStr)){
            return true;
        }
        String regEx = "[`~!@#$%^&*+=|{}':;',\\[\\].<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(inputStr);
        return m.find();
    }

    public static String getCharacterPinYin(char c)
    {
        String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c);
        if(pinyin == null) {
            return null;
        }
        return pinyin[0];
    }

    public static String convertToHtmlFormat(String content, int image_max_width) {
        String contentResult = removeTAG(content, "height=\"", "\"");
        contentResult = removeTAG(contentResult, "width=\"", "\"");
        contentResult = contentResult.replaceAll("<img", "<br/><img");
        contentResult = contentResult.replaceAll("/>", "/><br/>");
        contentResult = contentResult.replaceAll("<img", "<img width=\"" + image_max_width + "\" ");
        contentResult = contentResult.replaceAll("<body>", "<body><br/>");
        contentResult = contentResult.replaceAll("<link>","");
        contentResult = contentResult.replaceAll("</link>","");
        contentResult = contentResult.replaceAll("点击下载雪球手机客户端","");
        contentResult = contentResult.replaceAll("http://xueqiu.com/xz","");
        contentResult = contentResult.replaceAll("<br/><br/>","<br/>");
        return contentResult;
    }

    private static String removeTAG(String content, String START_TAG, String END_TAG) {
        if (content.contains(START_TAG)) {
            int start = content.indexOf(START_TAG);
            int end = content.indexOf(END_TAG, start + START_TAG.length());
            content = content.substring(0, start) + content.substring(end + END_TAG.length());
            return removeTAG(content, START_TAG, END_TAG);
        } else {
            return content;
        }
    }
}
