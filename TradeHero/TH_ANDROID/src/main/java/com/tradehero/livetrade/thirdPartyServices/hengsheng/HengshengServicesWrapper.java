package com.tradehero.livetrade.thirdPartyServices.hengsheng;

import android.app.Activity;

import com.tradehero.livetrade.data.LiveTradeBalanceDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustEnterDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePendingEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeConstants;
import com.tradehero.livetrade.services.LiveTradeServices;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengShengEntrustEnterDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBalanceDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBaseDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBusinessQryDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengEntrustQryDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengPositionDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengSessionDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengWithdrawEnterDTO;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.services.HengshengRequestCallback;
import com.tradehero.livetrade.thirdPartyServices.hengsheng.services.HengshengServiceAync;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton public class HengshengServicesWrapper implements LiveTradeServices {

    @NotNull private HengshengManager hengshengManager;
    @NotNull private final HengshengServiceAync service;

    @Inject public HengshengServicesWrapper(@NotNull HengshengServiceAync service,
                                            @NotNull HengshengManager hengshengManager) {
        this.service = service;
        this.hengshengManager = hengshengManager;
    }

    /**
     * 登录
     */
    @Override
    public void login(Activity activity,
                      String account,
                      String password,
                      final LiveTradeCallback<LiveTradeSessionDTO> callback
                      )
    {
        Callback<HengshengSessionDTO> cb = new Callback<HengshengSessionDTO>() {
            @Override
            public void success(HengshengSessionDTO hengshengSessionDTO, Response response) {
                hengshengManager.setSessionDTO(hengshengSessionDTO);
                LiveTradeSessionDTO dto = new LiveTradeSessionDTO();
                callback.onSuccess(dto);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
            }
        };

        service.login(HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                HengshengConstants.SENDER_COMP_ID,
                HengshengConstants.targetBusinsys,
                HengshengConstants.opStation,
                HengshengConstants.inputContent,
                account,
                password,
                cb);
    }

    @Override
    public void logout() {
        hengshengManager.setSessionDTO(null);
    }

    @Override
    public void signup() {
        // Do nothing. Does not support
    }

    /**
     * 获取账号资金
     */
    @Override
    public void getBalance(final LiveTradeCallback<LiveTradeBalanceDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengshengBalanceDTO> cb = new HengshengRequestCallback<HengshengBalanceDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradeBalanceDTO dto = LiveTradeBalanceDTO.parseHengshengDTO((HengshengBalanceDTO)hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };

            service.getBalance(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 获取仓位
     */
    @Override
    public void getPosition(final LiveTradeCallback<LiveTradePositionDTO> callback)
    {
        HengshengRequestCallback<HengshengPositionDTO> cb = new HengshengRequestCallback<HengshengPositionDTO>() {
            @Override
            public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                LiveTradePositionDTO dto = LiveTradePositionDTO.parseHengshengDTO((HengshengPositionDTO)hengshengBaseDTO);
                callback.onSuccess(dto);
            }

            @Override
            public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
            }
        };
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            service.getPosition(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 委托买入
     */
    @Override
    public void buy(String stockCode, int amount, float price, final LiveTradeCallback<LiveTradeEntrustEnterDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengShengEntrustEnterDTO> cb = new HengshengRequestCallback<HengShengEntrustEnterDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradeEntrustEnterDTO dto = LiveTradeEntrustEnterDTO.parseHengshengDTO((HengShengEntrustEnterDTO) hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };

            int exchangeType = 1;       // Todo
            service.entrustEnter(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    exchangeType,
                    "",
                    stockCode,
                    amount,
                    price,
                    HengshengConstants.EXCHANGE_BUY,
                    0,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 委托卖出
     */
    @Override
    public void sell(String stockCode, int amount, float price, final LiveTradeCallback<LiveTradeEntrustEnterDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengShengEntrustEnterDTO> cb = new HengshengRequestCallback<HengShengEntrustEnterDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradeEntrustEnterDTO dto = LiveTradeEntrustEnterDTO.parseHengshengDTO((HengShengEntrustEnterDTO) hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };

            int exchangeType = 1;       // Todo
            service.entrustEnter(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    exchangeType,
                    "",
                    stockCode,
                    amount,
                    price,
                    HengshengConstants.EXCHANGE_SELL,
                    0,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 查询可撤委托
     */
    @Override
    public void pendingEntrustQuery(final LiveTradeCallback<LiveTradePendingEntrustQueryDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengshengEntrustQryDTO> cb = new HengshengRequestCallback<HengshengEntrustQryDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradePendingEntrustQueryDTO dto = LiveTradePendingEntrustQueryDTO.parseHengshengDTO((HengshengEntrustQryDTO) hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };
            service.entrustQry(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    1,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 查询全部委托
     */
    @Override
    public void entrustQuery(final LiveTradeCallback<LiveTradeEntrustQueryDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengshengEntrustQryDTO> cb = new HengshengRequestCallback<HengshengEntrustQryDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradeEntrustQueryDTO dto = LiveTradeEntrustQueryDTO.parseHengshengDTO((HengshengEntrustQryDTO) hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };
            service.entrustQry(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    0,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 撤销委托
     */
    @Override
    public void entrustCancel(final String marketCode, final String entrustNo, final String entrustDate, final String withdrawCate, final String securityId, final LiveTradeCallback<LiveTradeEntrustCancelDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengshengWithdrawEnterDTO> cb = new HengshengRequestCallback<HengshengWithdrawEnterDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradeEntrustCancelDTO dto = LiveTradeEntrustCancelDTO.parseHengshengDTO((HengshengWithdrawEnterDTO) hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };
            service.withdrawEnter(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    entrustNo,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }

    /**
     * 当日成交
     */
    @Override
    public void dealQuery(final LiveTradeCallback<LiveTradeDealQueryDTO> callback)
    {
        String authStr = hengshengManager.getAccessToken();
        if (isSessionValid())
        {
            HengshengRequestCallback<HengshengBusinessQryDTO> cb = new HengshengRequestCallback<HengshengBusinessQryDTO>() {
                @Override
                public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    LiveTradeDealQueryDTO dto = LiveTradeDealQueryDTO.parseHengshengDTO((HengshengBusinessQryDTO) hengshengBaseDTO);
                    callback.onSuccess(dto);
                }

                @Override
                public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response) {
                    callback.onError(hengshengBaseDTO.error_code, hengshengBaseDTO.error_info);
                }

                @Override
                public void failure(RetrofitError error) {
                    callback.onError(LiveTradeConstants.ERROR_CODE_RETROFIT, error.getLocalizedMessage());
                }
            };

            int exchangeType = HengshengConstants.EXCHANGE_TYPE_SHANGHAI;
            service.businessQry(authStr,
                    HengshengConstants.TARGET_COMP_ID_HENGSHENG,
                    HengshengConstants.SENDER_COMP_ID,
                    exchangeType,
                    cb);
        }
        else
        {
            callback.onError(LiveTradeConstants.ERROR_CODE_SESSION_OUT, LiveTradeConstants.ERROR_CODE_SESSION_OUT);
        }
    }


    @Override
    public boolean isSessionValid()
    {
        String authStr = hengshengManager.getAccessToken();
        if (authStr.equals(HengshengManager.SESSION_TIME_OUT))
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
