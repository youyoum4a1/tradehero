package com.tradehero.th.utils.yahoo;

/**
 * Created by julien on 9/10/13
 */

public enum ChartSize
{
    small("s"),
    medium("m"),
    large("l");

    private final String code;

    private ChartSize(String c) {
        code = c;
    }

    public boolean equalsCode(String otherCode){
        return (otherCode == null)? false:code.equals(otherCode);
    }

    public String toString(){
        return code;
    }
}