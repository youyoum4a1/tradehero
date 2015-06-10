package com.tradehero.chinabuild.data;

/**
 * Created by liangyx on 6/8/15.
 */
public class QuoteTick {
    public Integer id;
    public Double price;
    public Long volume;
    public Double avgPrice;

    /**
     * set Price
     * @param p
     */
    public void setP(Double p) {
        price = p;
    }

    /**
     * set volume
     * @param v
     */
    public void setV(Long v) {
        volume = v;
    }

    /**
     * set average price
     * @param a
     */
    public void setA(Double a) {
        avgPrice = a;
    }

    public String toString() {
        return String.format("id[%d], price[%f], volume[%d], avg[%f]", id, price, volume, avgPrice);
    }

}
