package com.tradehero.th.network.retrofit;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.Server;
import retrofit.client.Client;
import retrofit.converter.Converter;

import static org.mockito.Mockito.mock;

@Module(
        library = true,
        complete = false,
        overrides = true
)
public class RetrofitTestModule
{
    @Provides RestAdapter.Builder provideRestAdapterBuilder(Converter converter)
    {
        return new RestAdapter.Builder()
                .setConverter(converter)
                .setClient(mock(Client.class));
    }

    /** Let's spam ourselves, not the real server **/
    @Provides @Singleton Server provideApiServer()
    {
        return new Server("http://localhost/");
    }

    @Provides @Singleton RestAdapter provideRestAdapter(RestAdapter.Builder builder, Server server, RequestHeaders requestHeaders)
    {
        return builder
                .setServer(server)
                .setRequestInterceptor(new MockRequestInterceptor(requestHeaders))
                .build();
    }

    private static final class MockRequestInterceptor implements RequestInterceptor
    {
        private RequestHeaders requestHeaders;

        public MockRequestInterceptor(RequestHeaders requestHeaders)
        {
            this.requestHeaders = requestHeaders;
        }

        @Override public void intercept(RequestFacade requestFacade)
        {
            requestHeaders.intercept(requestFacade);
            requestFacade.addHeader("WHOAMI", System.getenv("USERNAME") + System.getenv("USERDOMAIN"));
        }
    }
}
