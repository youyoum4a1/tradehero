package com.tradehero.common.billing.Alipay;
/**
 * Created by tradehero on 14-2-26.
 */
public class AlipayConstant
{
    //trade status
    public static final int TRADE_STATUS_WAIT_BUYER_PAY = 0;//not pay
    public static final int TRADE_STATUS_TRADE_SUCCESS = 1;//can cancel; after cancel, no fee back
    public static final int TRADE_STATUS_TRADE_FINISHED = 2;//cannot cancel; after cancel, no fee back
    //after cancel
    public static final int TRADE_STATUS_TRADE_CLOSED = 3;//the fee will back
    //error code
    public static final int ERROR_CODE_TRADE_SUCCESS = 9000;
    public static final int ERROR_CODE_PROCESSING = 8000;
    public static final int ERROR_CODE_TRADE_PAY_FAILED = 4000;
    public static final int ERROR_CODE_TRADE_CANCEL_BY_USER = 6001;
    public static final int ERROR_CODE_NETWORK_ERROR = 6002;
    //result status
    public static final int RESULT_STATUS_PARAMETER_ERROR = 4001;//4003,4004,4005,4006,6000,7001 need check

    //合作身份者id，以2088开头的16位纯数字
    public static final String DEFAULT_PARTNER = "2088101568358171";
    //收款支付宝账号
    public static final String DEFAULT_SELLER = "alipay-test09@alipay.com";
    //
    // 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
    // 这里签名时，只需要使用生成的RSA私钥。
    // Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
    //商户私钥
    public static final String PRIVATE = "";
    //do not change !!!
    public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    public static final String NOTIFY_URL = "";//when bill finished, alipay will notify this

}
