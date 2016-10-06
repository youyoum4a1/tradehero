package com.androidth.general.models.sms.twilio;

import android.util.Base64;
import com.androidth.general.common.log.RetrofitErrorHandlerLogger;
import dagger.Module;
import dagger.Provides;
import java.io.UnsupportedEncodingException;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true
)
public class TwilioModule
{
    private RestAdapter createTwilioRestAdapter(
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
                            request.addHeader("Authorization", "Basic " + Base64.encodeToString(
                                    (TwilioConstants.TWILIO_TH_ACCOUNT + ":" + TwilioConstants.TWILIO_TH_PASSWORD).getBytes("UTF-8"), Base64.NO_WRAP));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                })
                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides TwilioServiceRx provideTwilioServiceRx(RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return createTwilioRestAdapter(builder, errorHandlerLogger).create(TwilioServiceRx.class);
    }
}
