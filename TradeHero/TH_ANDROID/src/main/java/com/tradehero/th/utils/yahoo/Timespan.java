package com.tradehero.th.utils.yahoo;

/**
 * Created by julien on 9/10/13
 */
public enum Timespan
{
    day1("1d"),
    days5("5d"),
    months3("3m"),
    months6("6m"),
    year1("1y"),
    year2("2y"),
    year5("5y"),
    yearMax("my");

    private final String code;

    private Timespan(String c) {
        code = c;
    }

    public boolean equalsCode(String otherCode){
        return (otherCode == null)? false:code.equals(otherCode);
    }

    public String toString(){
        return code;
    }
}