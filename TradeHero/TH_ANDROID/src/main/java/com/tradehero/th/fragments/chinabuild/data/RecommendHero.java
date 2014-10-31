package com.tradehero.th.fragments.chinabuild.data;

/**
 * Created by palmer on 14-10-30.
 */
public class RecommendHero {

    public int id;

    public String name;

    //Rate of return
    public double roi;

    public String description;

    public String picUrl;

    @Override
    public String toString(){
        return id + "  " + name + "  " + roi + "  " + description + "  " + picUrl;
    }

}
