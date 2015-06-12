package com.tradehero.chinabuild.data;

import java.util.Date;

/**
 * Created by liangyx on 6/11/15.
 */
public class SecurityComment {
    public Integer commentId;
    public Integer userId;
    public String userName;
    public String userPicUrl;
    public String text;
    public Date createdAtUtc;
    public Integer replyCount;
    public Integer upVoteCount;
    public Boolean upVoted;
}
