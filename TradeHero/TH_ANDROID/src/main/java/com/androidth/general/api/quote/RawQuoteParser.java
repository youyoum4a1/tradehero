package com.androidth.general.api.quote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidth.general.api.RawResponseParser;
import com.androidth.general.api.SignatureContainer;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.security.LiveSignatureContainer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

public class RawQuoteParser extends RawResponseParser
        implements Func1<Response<ResponseBody>, Observable<? extends LiveQuoteDTO>>
{
    @NonNull private JacksonConverterFactory converter;
    @NonNull private Retrofit retrofit;
    @NonNull private ObjectMapper objectMapper;

    //<editor-fold desc="Constructors">
    @Inject public RawQuoteParser(@NonNull JacksonConverterFactory converter,
                                  @NonNull Retrofit retrofit,
                                  @NonNull ObjectMapper objectMapper)
    {
        this.converter = converter;
        this.retrofit = retrofit;
        this.objectMapper = objectMapper;
    }
    //</editor-fold>

    @Nullable public LiveQuoteDTO parse(@NonNull Response<ResponseBody> response) throws IOException
    {
        String rawResponse = "";
        try{
            rawResponse = getResponseBodyToString(response.body());
        }catch (Exception e){
            throw new IOException("Contents not found");
        }
        SignatureContainer signatureContainer = objectMapper.readValue(rawResponse, SignatureContainer.class);

        //wont work because ResponseBody stream disappears
//        Converter<ResponseBody, SignatureContainer> containerConverter = retrofit.responseBodyConverter(SignatureContainer.class, new Annotation[0]);
//        SignatureContainer signatureContainer = containerConverter.convert(responseBody);

        LiveQuoteDTO quoteDTO = null;
        if(signatureContainer!=null){
            quoteDTO = signatureContainer.signedObject;
            if(quoteDTO!=null){
                quoteDTO.setRawResponse(rawResponse);
            }
        }
        return quoteDTO;
    }

    @Override public Observable<? extends LiveQuoteDTO> call(@NonNull Response<ResponseBody> response)
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
}