package com.tradehero.th.fragments.chinabuild.data;

/**
 * Created by palmer on 14-10-21.
 */
public class AppInfoDTO {

    public boolean suggestUpgrade;
    public boolean forceUpgrade;
    public String latestVersionDownloadUrl;


    public boolean isSuggestUpgrade() {
        return suggestUpgrade;
    }

    public void setSuggestUpgrade(boolean suggestUpgrade) {
        this.suggestUpgrade = suggestUpgrade;
    }

    public boolean isForceUpgrade() {
        return forceUpgrade;
    }

    public void setForceUpgrade(boolean forceUpgrade) {
        this.forceUpgrade = forceUpgrade;
    }

    public String getLatestVersionDownloadUrl() {
        return latestVersionDownloadUrl;
    }

    public void setLatestVersionDownloadUrl(String latestVersionDownloadUrl) {
        this.latestVersionDownloadUrl = latestVersionDownloadUrl;
    }


    @Override
    public String toString(){
        String str = "latestVersionDownloadUrl : " + latestVersionDownloadUrl;
        if(suggestUpgrade){
            str = str + " suggestUpgrade : true";
        }else{
            str = str + " suggestUpgrade : false";
        }
        if(forceUpgrade){
            str = str + " forceUpgrade : true";
        }else{
            str = str + " forceUpgrade : false";
        }
        return str;
    }
}
