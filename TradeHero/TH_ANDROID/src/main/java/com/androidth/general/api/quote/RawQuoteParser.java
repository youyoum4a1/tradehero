package com.androidth.general.api.quote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.api.RawResponseParser;
import com.androidth.general.api.SignatureContainer;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.security.LiveSignatureContainer;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

public class RawQuoteParser extends RawResponseParser
        implements Func1<Response<SignatureContainer>, Observable<? extends LiveQuoteDTO>>
{
    @NonNull private JacksonConverterFactory converter;
    @NonNull private Retrofit retrofit;

    //<editor-fold desc="Constructors">
    @Inject public RawQuoteParser(@NonNull JacksonConverterFactory converter, @NonNull Retrofit retrofit)
    {
        this.converter = converter;
        this.retrofit = retrofit;
    }
    //</editor-fold>

    @Nullable public LiveQuoteDTO parse(@NonNull Response<SignatureContainer> response) throws IOException
    {
//        LiveQuoteDTO quoteDTO = null;
//        TypedByteArray body = getBodyAsTypedArray(response);
//        if (body != null)
//        {
//            QuoteSignatureContainer signatureContainer = (QuoteSignatureContainer) converter.fromBody(body, QuoteSignatureContainer.class);
//            if (signatureContainer != null)
//            {
//                quoteDTO = signatureContainer.signedObject;
//                if (quoteDTO != null)
//                {
//                    quoteDTO.setRawResponse(new String(body.getBytes()));
//                }
//            }
//        }

        //Retrofit 2 way
//        Converter<ResponseBody, ?> converter1 = this.converter
//                .responseBodyConverter(null, new Annotation[0], retrofit);converter1.convert(response.)
//        LiveQuoteDTO quoteDTO = (LiveQuoteDTO) response.body();


        LiveQuoteDTO quoteDTO = null;
        if(response.body()!=null){
            quoteDTO = response.body().signedObject;
            if(quoteDTO!=null){
                quoteDTO.setRawResponse(new String(response.body().toString().getBytes()));

                return quoteDTO;
            }
        }
        return null;
    }

    @Override public Observable<? extends LiveQuoteDTO> call(@NonNull Response<SignatureContainer> response)
    {
        try
        {
            LiveQuoteDTO parsed = parse(response);
            return Observable.just(parsed);
        } catch (Throwable e)
        {
            return Observable.error(e);
        }
    }

//    private static class QuoteSignatureContainer extends SignatureContainer<LiveQuoteDTO>
//    {
//    }
}