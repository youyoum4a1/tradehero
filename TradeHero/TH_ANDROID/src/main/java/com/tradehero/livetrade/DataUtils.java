package com.tradehero.livetrade;

import java.text.DecimalFormat;

/**
 * Created by palmer on 15/7/20.
 */
public class DataUtils {

    public static String keepInteger(double value){
        DecimalFormat df = new DecimalFormat("#0");
        return df.format(value);
    }

    public static String keepOneDecimal(double value){
        DecimalFormat df = new DecimalFormat("#0.0");
        return df.format(value);
    }

    public static String keepTwoDecimal(double value){
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(value);
    }
}
