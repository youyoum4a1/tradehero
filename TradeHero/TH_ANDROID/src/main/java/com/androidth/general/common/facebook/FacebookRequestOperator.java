package com.androidth.general.common.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import rx.Observable;
import rx.Subscriber;
import rx.android.internal.Assertions;
import timber.log.Timber;

/**
 * It should be noted that only one Facebook Request can run at a time
 */
public class FacebookRequestOperator implements Observable.OnSubscribe<Response>
{
    @NonNull private final Session session;
    @NonNull private final String graphPath;
    @Nullable private final Bundle parameters;
    @Nullable private final HttpMethod httpMethod;
    @Nullable private final String version;

    //<editor-fold desc="Constructors">
    private FacebookRequestOperator(@NonNull Builder builder)
    {
        this.session = builder.session;
        this.graphPath = builder.graphPath;
        this.parameters = builder.parameters;
        this.httpMethod = builder.httpMethod;
        this.version = builder.version;
    }
    //</editor-fold>

    @Override public void call(final Subscriber<? super Response> subscriber)
    {
        Assertions.assertUiThread();
        Request request = new Request(
                session,
                graphPath,
                parameters,
                httpMethod,
                new Request.Callback()
                {
                    @Override public void onCompleted(Response response)
                    {
                        FacebookRequestError e = response.getError();
                        if (e != null)
                        {
                            subscriber.onError(new FacebookRequestException(e));
                        }
                        else
                        {
                            subscriber.onNext(response);
                            subscriber.onCompleted();
                        }
                    }
                },
                version);
        Timber.d("Facebook request version %s", request.getVersion());
        Request.executeBatchAsync(request);
    }

    public static class Builder
    {
        @NonNull private final Session session;
        @NonNull private String graphPath;
        @Nullable private Bundle parameters;
        @Nullable private HttpMethod httpMethod;
        @Nullable private String version;

        //<editor-fold desc="Constructors">
        private Builder(@NonNull Session session, @NonNull String graphPath)
        {
            this.session = session;
            this.graphPath = graphPath;
        }
        //</editor-fold>

        @NonNull public Builder setParameters(@NonNull Bundle parameters)
        {
            this.parameters = parameters;
            return this;
        }

        @NonNull public Builder setHttpMethod(@NonNull HttpMethod httpMethod)
        {
            this.httpMethod = httpMethod;
            return this;
        }

        @NonNull public Builder setVersion(@NonNull String version)
        {
            this.version = version;
            return this;
        }

        @NonNull public FacebookRequestOperator build()
        {
            return new FacebookRequestOperator(this);
        }
    }

    @NonNull public static Builder builder(@NonNull Session session, @NonNull String graphPath)
    {
        return new Builder(session, graphPath);
    }
}
