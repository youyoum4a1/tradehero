package com.androidth.general.models.sms.nexmo;

import com.androidth.general.common.log.RetrofitErrorHandlerLogger;
import com.androidth.general.models.sms.twilio.TwilioConstants;
import com.androidth.general.models.sms.twilio.TwilioServiceRx;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true
)
public class NexmoModule
{
    private RestAdapter createNexmoRestAdapter(
            RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
                .setEndpoint(Endpoints.newFixedEndpoint(TwilioConstants.TWILIO_API_ENDPOINT))
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override public void intercept(RequestFacade request)
                    {
                        try
                        {
//                            request.addHeader("Authorization", "Basic " + Base64.encodeToString(
//                                    (TwilioConstants.TWILIO_TH_ACCOUNT + ":" + TwilioConstants.TWILIO_TH_PASSWORD).getBytes("UTF-8"), Base64.NO_WRAP));
//                            request.addQueryParam("api_key", TwilioConstants.TWILIO_TH_ACCOUNT);
//                            request.addQueryParam("api_secret", TwilioConstants.TWILIO_TH_PASSWORD);
//                            request.addQueryParam("api_key", TwilioConstants.TWILIO_TH_ACCOUNT);
//                            request.addQueryParam("api_key", TwilioConstants.TWILIO_TH_ACCOUNT);
//                            request.addQueryParam("api_key", TwilioConstants.TWILIO_TH_ACCOUNT);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides NexmoServiceRx provideNexmoServiceRx(RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return createNexmoRestAdapter(builder, errorHandlerLogger).create(NexmoServiceRx.class);
    }
}
