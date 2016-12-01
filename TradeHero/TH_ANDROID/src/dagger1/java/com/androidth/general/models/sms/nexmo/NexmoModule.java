package com.androidth.general.models.sms.nexmo;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module(
        complete = false,
        library = true
)
public class NexmoModule
{
    private Retrofit createNexmoRestAdapter(
            Retrofit.Builder builder
//            RetrofitErrorHandlerLogger errorHandlerLogger
    )
    {
        return builder
//                .setEndpoint(Endpoints.newFixedEndpoint(NexmoConstants.API_URL_ENDPOINT))
                .baseUrl(NexmoConstants.API_URL_ENDPOINT)
//                .setRequestInterceptor(new RequestInterceptor()
//                {
//                    @Override public void intercept(RequestFacade request)
//                    {
//                        try
//                        {
////                            request.addHeader("Authorization", "Basic " + Base64.encodeToString(
////                                    (TwilioConstants.API_KEY + ":" + TwilioConstants.API_SECRET).getBytes("UTF-8"), Base64.NO_WRAP));
////                            request.addQueryParam("api_key", TwilioConstants.API_KEY);
////                            request.addQueryParam("api_secret", TwilioConstants.API_SECRET);
////                            request.addQueryParam("api_key", TwilioConstants.API_KEY);
////                            request.addQueryParam("api_key", TwilioConstants.API_KEY);
////                            request.addQueryParam("api_key", TwilioConstants.API_KEY);
//                        } catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                })
//                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides NexmoServiceRx provideNexmoServiceRx(Retrofit.Builder builder
//            RetrofitErrorHandlerLogger errorHandlerLogger
    )
    {
        return createNexmoRestAdapter(builder).create(NexmoServiceRx.class);
    }
}
