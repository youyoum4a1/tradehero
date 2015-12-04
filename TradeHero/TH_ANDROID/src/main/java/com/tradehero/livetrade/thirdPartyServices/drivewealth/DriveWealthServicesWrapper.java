package com.tradehero.livetrade.thirdPartyServices.drivewealth;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.data.LiveTradeBalanceDTO;
import com.tradehero.livetrade.data.LiveTradeDealQueryDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustCancelDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustEnterDTO;
import com.tradehero.livetrade.data.LiveTradeEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePendingEntrustQueryDTO;
import com.tradehero.livetrade.data.LiveTradePositionDTO;
import com.tradehero.livetrade.data.LiveTradeSessionDTO;
import com.tradehero.livetrade.services.LiveTradeCallback;
import com.tradehero.livetrade.services.LiveTradeServices;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthErrorDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthLoginBody;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSessionResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupBody;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupLiveBody;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthUploadResultDTO;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.services.DriveWealthServiceAync;
import com.tradehero.th.R;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
@Singleton public class DriveWealthServicesWrapper implements LiveTradeServices {

    private DriveWealthServiceAync mServices;
    private DriveWealthManager mManager;

    public static final String DW_SIGNUP_SUCCESS = "DWSignup.success";
    public static final String DW_SIGNUP_FAILED = "DWSignup.failed";

    @Inject public DriveWealthServicesWrapper(@NotNull DriveWealthServiceAync service,
                                              @NotNull DriveWealthManager manager) {
        mServices = service;
        mManager = manager;
    }

    @Override
    public boolean needCheckPhoneNumber() {
        return false;
    }

    @Override
    public boolean isSessionValid() {
        return false;
    }

    @Override
    public void login(Activity activity, String account, String password, final LiveTradeCallback<LiveTradeSessionDTO> callback) {
        Callback<DriveWealthSessionResultDTO> cb = new Callback<DriveWealthSessionResultDTO>() {

            @Override
            public void success(DriveWealthSessionResultDTO driveWealthSessionResultDTO, Response response) {
                mManager.setUserID(driveWealthSessionResultDTO.userID);
                mManager.setSessionKey(driveWealthSessionResultDTO.sessionKey);
                callback.onSuccess(new LiveTradeSessionDTO());
            }

            @Override
            public void failure(RetrofitError error) {
                DriveWealthErrorDTO dwError = retrofitErrorToDriveWealthError(error);
                callback.onError(String.valueOf(dwError.code), dwError.message);
            }
        };

        mServices.login(new DriveWealthLoginBody(account, password), cb);
    }

    @Override
    public void logout() {

    }

    @Override
    public void signup(Activity activity) {

    }

    @Override
    public void getBalance(LiveTradeCallback<LiveTradeBalanceDTO> callback) {

    }

    @Override
    public void getPosition(LiveTradeCallback<LiveTradePositionDTO> callback) {

    }

    @Override
    public void buy(String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {

    }

    @Override
    public void sell(String stockCode, int amount, float price, LiveTradeCallback<LiveTradeEntrustEnterDTO> callback) {

    }

    @Override
    public void pendingEntrustQuery(LiveTradeCallback<LiveTradePendingEntrustQueryDTO> callback) {

    }

    @Override
    public void entrustQuery(LiveTradeCallback<LiveTradeEntrustQueryDTO> callback) {

    }

    @Override
    public void entrustCancel(String marketCode, String entrustNo, String entrustDate, String withdrawCate, String securityId, LiveTradeCallback<LiveTradeEntrustCancelDTO> callback) {

    }

    @Override
    public void dealQuery(LiveTradeCallback<LiveTradeDealQueryDTO> callback) {

    }

    public MiddleCallback<DriveWealthErrorDTO> checkUserName(String userName, Callback<DriveWealthErrorDTO> callback)
    {
        MiddleCallback<DriveWealthErrorDTO> middleCallback = new BaseMiddleCallback<>(callback);
        mServices.checkUserName(userName, middleCallback);
        return middleCallback;
    }

    public void processSignupLive(final Activity activity) {
        DriveWealthSignupFormDTO formDTO = mManager.getSignupFormDTO();

        login(activity, formDTO.userName, formDTO.password, new LiveTradeCallback<LiveTradeSessionDTO>() {
            @Override
            public void onSuccess(LiveTradeSessionDTO liveTradeSessionDTO) {
                // Use the existing account to sign up Live.
                signupLive(activity);
            }

            @Override
            public void onError(String errorCode, String errorContent) {
                // Sign up a free account then to sign up Live.
                signupFree(activity);
            }
        });
    }

    private void signupFree(final Activity activity) {
        DriveWealthSignupFormDTO formDTO = mManager.getSignupFormDTO();

        Callback<DriveWealthSignupResultDTO> cb = new Callback<DriveWealthSignupResultDTO>() {

            @Override
            public void success(DriveWealthSignupResultDTO driveWealthSessionResultDTO, Response response) {
                mManager.setUserID(driveWealthSessionResultDTO.userID);
                processSignupLive(activity);
            }

            @Override
            public void failure(RetrofitError error) {
                DriveWealthErrorDTO dwError = retrofitErrorToDriveWealthError(error);
                THToast.show(dwError.message);
                activity.sendBroadcast(new Intent(DW_SIGNUP_FAILED));
            }
        };

        mServices.signup(new DriveWealthSignupBody(formDTO.email, formDTO.firstNameInEng, formDTO.lastNameInEng, formDTO.userName, formDTO.password), cb);
    }

    private void signupLive(final Activity activity) {
        DriveWealthSignupFormDTO formDTO = mManager.getSignupFormDTO();

        Callback<DriveWealthSignupResultDTO> cb = new Callback<DriveWealthSignupResultDTO>() {

            @Override
            public void success(DriveWealthSignupResultDTO driveWealthSessionResultDTO, Response response) {
                uploadIdCardFront(activity);
            }

            @Override
            public void failure(RetrofitError error) {
                DriveWealthErrorDTO dwError = retrofitErrorToDriveWealthError(error);
                THToast.show(dwError.message);
                activity.sendBroadcast(new Intent(DW_SIGNUP_FAILED));
            }
        };

        String[] employmentArray = activity.getResources().getStringArray(R.array.dw_signup_employment_in_eng);
        String[] businessArray = activity.getResources().getStringArray(R.array.dw_signup_industry_in_eng);
        String[] investObjArray = activity.getResources().getStringArray(R.array.dw_investment_objectives_in_eng);
        String[] investExpArray = activity.getResources().getStringArray(R.array.dw_investment_experience_in_eng);
        String[] incomeArray = activity.getResources().getStringArray(R.array.dw_annual_income);
        String[] networthLiquidArray = activity.getResources().getStringArray(R.array.dw_networth_liquid);
        String[] networthTotalArray = activity.getResources().getStringArray(R.array.dw_networth_total);
        String[] riskToleranceArray = activity.getResources().getStringArray(R.array.dw_risk_tolerance_in_eng);
        String[] timeHorizonArray = activity.getResources().getStringArray(R.array.dw_time_horizon_in_eng);
        String[] liquidityNeedsArray = activity.getResources().getStringArray(R.array.dw_liquidity_needs_in_eng);

        mServices.signupLive(
                new DriveWealthSignupLiveBody(
                        mManager.getUserID(), formDTO.firstNameInEng, formDTO.lastNameInEng,
                        formDTO.idNO, formDTO.address, formDTO.email,
                        employmentArray[formDTO.employmentStatusIdx], businessArray[formDTO.employerBusinessIdx],
                        formDTO.employerCompany, formDTO.employerIsBroker,
                        formDTO.director, formDTO.politicallyExposed,
                        investObjArray[formDTO.investmentObjectivesIdx], investExpArray[formDTO.investmentExperienceIdx],
                        incomeArray[formDTO.annualIncomeIdx], networthLiquidArray[formDTO.networthLiquidIdx],
                        networthTotalArray[formDTO.networthTotalIdx], riskToleranceArray[formDTO.riskToleranceIdx],
                        timeHorizonArray[formDTO.timeHorizonIdx], liquidityNeedsArray[formDTO.liquidityNeedsIdx],
                        formDTO.ackSignedBy),
                cb);
    }

    private void uploadIdCardFront(final Activity activity) {
        DriveWealthSignupFormDTO formDTO = mManager.getSignupFormDTO();

        Callback<DriveWealthUploadResultDTO> cb = new Callback<DriveWealthUploadResultDTO>() {

            @Override
            public void success(DriveWealthUploadResultDTO driveWealthSessionResultDTO, Response response) {
                uploadIdCardBack(activity);
            }

            @Override
            public void failure(RetrofitError error) {
                DriveWealthErrorDTO dwError = retrofitErrorToDriveWealthError(error);
                THToast.show(dwError.message);
                activity.sendBroadcast(new Intent(DW_SIGNUP_FAILED));
            }
        };

        mServices.uploadFile(mManager.getSessionKey(),
                new TypedString(mManager.getUserID()),
                new TypedString("Picture ID_Proof of address"),
                new TypedFile("image/png", new File(formDTO.idcardFront.getPath())),
                cb);
    }

    private void uploadIdCardBack(final Activity activity) {
        DriveWealthSignupFormDTO formDTO = mManager.getSignupFormDTO();

        Callback<DriveWealthUploadResultDTO> cb = new Callback<DriveWealthUploadResultDTO>() {

            @Override
            public void success(DriveWealthUploadResultDTO driveWealthSessionResultDTO, Response response) {
                activity.sendBroadcast(new Intent(DW_SIGNUP_SUCCESS));
            }

            @Override
            public void failure(RetrofitError error) {
                DriveWealthErrorDTO dwError = retrofitErrorToDriveWealthError(error);
                THToast.show(dwError.message);
                activity.sendBroadcast(new Intent(DW_SIGNUP_FAILED));
            }
        };

        mServices.uploadFile(mManager.getSessionKey(),
                new TypedString(mManager.getUserID()),
                new TypedString("Picture ID_Proof of address"),
                new TypedFile("image/png", new File(formDTO.idcardBack.getPath())),
                cb);
    }

    private DriveWealthErrorDTO retrofitErrorToDriveWealthError(RetrofitError error) {
        DriveWealthErrorDTO dwError = null;

        if (error != null &&
                error.getResponse() != null &&
                error.getResponse().getBody() != null) {
            String bodyString = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
            if (bodyString != null) {
                dwError = (DriveWealthErrorDTO) THJsonAdapter.getInstance().fromBody(bodyString, DriveWealthErrorDTO.class);
            }
        }

        if (dwError == null) {
            dwError = new DriveWealthErrorDTO();
            dwError.message = error.getLocalizedMessage();
        }

        return dwError;
    }
}
