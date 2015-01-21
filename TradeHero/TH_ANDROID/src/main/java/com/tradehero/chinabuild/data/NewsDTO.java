package com.tradehero.chinabuild.data;

import java.util.Date;

/**
 * Created by palmer on 15/1/15.
 */
public class NewsDTO {

    public String title = "";
    public long id = -1;
    public int commentCount = 0;
    public Date createdAtUtc;
    public int upvoteCount = 0;
    public int downvoteCount = 0;
    public int voteDirection = 0;
    public String langCode = "";

    @Override
    public String toString(){
        return "title: " + title + "  id: " + id + "  createdAtUtc: " + createdAtUtc;
    }

}
