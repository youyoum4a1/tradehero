package com.tradehero.th.wxapi;


/**
 * Created by palmer on 15/6/16.
 */
public class WXMessage {

    private static String[] WX_SHARE_SECURITY_Messages = new String[]{
            "炒炒股票更健康，重点关注<security>",
            "今日必看个股——<security>",
            "小伙伴来看看我的<security>怎么样？",
            "看看我的股神推荐，<security>",
            "今日荐股——<security>",
            "<security>，好股别错过哦"
    };

    private static String getSecurityShareMessage(String securityName){
        int randomNum =  (int) (Math.random() * 100);
        int index = randomNum%(WX_SHARE_SECURITY_Messages.length);
        String message = WX_SHARE_SECURITY_Messages[index].replace("<security>", securityName);
        return message;
    }

    public static String getSecurityShareMessage(int percent, String securityName){
        String message = "";
        if(percent >= 4){
            message = "涨了" + percent +"%哦，<security>棒棒哒";
            message = message.replace("<security>", securityName);
        } else if(percent <= -4){
            String percentStr = String.valueOf(percent).replace("-", "");
            message = "<security>不好了呢，跌幅"+percentStr+"%";
            message = message.replace("<security>", securityName);
        } else {
            message = getSecurityShareMessage(securityName);
        }
        return message;
    }
}
