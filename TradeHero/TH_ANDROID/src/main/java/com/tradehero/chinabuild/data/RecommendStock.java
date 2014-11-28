package com.tradehero.chinabuild.data;

/**
 * Created by palmer on 14-10-30.
 */
public class RecommendStock {

    public int id;

    public String name;

    public double lastPrice;

    public double risePercent;

    public int holdCount;

    public String currencyDisplay;

    @Override
    public String toString(){
        return id + "  " + name +  "  " + lastPrice + "  " + risePercent + "  " + holdCount + "  " + currencyDisplay;
    }

}
