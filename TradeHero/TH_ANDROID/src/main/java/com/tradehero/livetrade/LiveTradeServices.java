package com.tradehero.livetrade;

/**
 * Created by Sam on 15/8/27.
 */
public interface LiveTradeServices {

    boolean isSessionValid();

    void login();

    void logout();

    void signup();

    void getBalance() ;

    void getPosition();

    void buy();

    void sell();

    void entrustQuery();

    void entrustCancel();

    void bargainQuery();

}
