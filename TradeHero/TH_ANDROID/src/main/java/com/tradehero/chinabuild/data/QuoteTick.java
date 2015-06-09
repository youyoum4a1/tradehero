package com.tradehero.chinabuild.data;

/**
 * Created by liangyx on 6/8/15.
 */
public class QuoteTick {
    public Integer id;
    public Double p;
    public Long v;
    public Double a;

    public String toString() {
        return String.format("id[%d], price[%f], volume[%d], avg[%f]", id, p, v, a);
    }

}
