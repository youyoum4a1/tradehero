package com.tradehero.chinabuild.data;

import java.text.DecimalFormat;

/**
 * Created by liangyx on 6/8/15.
 */
public class QuoteDetail {

    private static DecimalFormat df = new DecimalFormat("#0.00");

    public String symb;
    public String name;
    public Double prec;
    public Double open;
    public Double vol;
    public Long amou;
    public Double high;
    public Double low;
    public Double last;
    public Double close;
    public Double bp1;
    public Integer bv1;
    public Double sp1;
    public Integer sv1;
    public Double bp2;
    public Integer bv2;
    public Double sp2;
    public Integer sv2;
    public Double bp3;
    public Integer bv3;
    public Double sp3;
    public Integer sv3;
    public Double bp4;
    public Integer bv4;
    public Double sp4;
    public Integer sv4;
    public Double bp5;
    public Integer bv5;
    public Double sp5;
    public Integer sv5;
    public String time;
    public String Id;

    public double getRiseRate() {
        if ((last == null) || (prec == null) || (prec == 0)) {
            return 0;
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        return Double.valueOf(df.format((last - prec) / prec));
    }


    public double getRise(){
        if ((last == null) || (prec == null) || (prec == 0)) {
            return 0;
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        return Double.valueOf(df.format(last - prec));
    }

    public String getPriceDifferent() {
        if ((last == null) || (prec == null) || (prec == 0)) {
            return "- -";
        }
        double d1 = last - prec;
        return df.format(d1);
    }
}
