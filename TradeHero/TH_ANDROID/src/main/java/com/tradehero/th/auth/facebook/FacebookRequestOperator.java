package com.tradehero.th.auth.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import rx.Observable;
import rx.Subscriber;

public class FacebookRequestOperator implements Observable.OnSubscribe<Response>
{
    @NonNull private final Session session;
    @Nullable private final String graphPath;
    @Nullable private final Bundle parameters;
    @Nullable private final HttpMethod httpMethod;
    @Nullable private final String version;

    private FacebookRequestOperator(@NonNull Builder builder)
    {
        this.session = builder.session;
        this.graphPath = builder.graphPath;
        this.parameters = builder.parameters;
        this.httpMethod = builder.httpMethod;
        this.version = builder.version;
    }

    @Override public void call(Subscriber<? super Response> subscriber)
    {
        new Request(
                session,
                graphPath,
                parameters,
                httpMethod,
                subscriber::onNext,
                version)
                .executeAsync();
    }

    public static class Builder
    {
        @NonNull private final Session session;
        @Nullable private String graphPath;
        @Nullable private Bundle parameters;
        @Nullable private HttpMethod httpMethod;
        @Nullable private String version;

        private Builder(@NonNull Session session)
        {
            this.session = session;
        }

        public Builder setGraphPath(@Nullable String graphPath)
        {
            this.graphPath = graphPath;
            return this;
        }

        public Builder setParameters(@Nullable Bundle parameters)
        {
            this.parameters = parameters;
            return this;
        }

        public Builder setHttpMethod(@Nullable HttpMethod httpMethod)
        {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder setVersion(@Nullable String version)
        {
            this.version = version;
            return this;
        }

        @NonNull public FacebookRequestOperator build()
        {
            return new FacebookRequestOperator(this);
        }
    }

    @NonNull public static Builder builder(@NonNull Session session)
    {
        return new Builder(session);
    }
}
