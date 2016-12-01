package com.androidth.general.utils;

import android.support.annotation.NonNull;

import com.androidth.general.models.retrofit2.THRetrofitException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils
{
    @NonNull public static List<String> getElements(@NonNull Throwable exception)
    {
        List<String> reported = new ArrayList<>();

        reported.add(exception.getClass().getName());
        reported.add(exception.getMessage());
        reported.addAll(getStacktrace(exception));

        return reported;
    }

    @NonNull public static List<String> getStacktrace(@NonNull Throwable exception)
    {
        List<String> reported = new ArrayList<>();

        for (StackTraceElement stackTraceElement : exception.getStackTrace())
        {
            reported.add(stackTraceElement.toString());
        }

        return reported;
    }

    @NonNull public static String getStringElementFromThrowable(@NonNull Throwable throwable, @NonNull String identifier){

        if(throwable==null){
            return "Cannot parse the error message";
        }
        if(throwable instanceof THRetrofitException){
            try{
                THRetrofitException err = (THRetrofitException) throwable;
//                String string =  new String(((TypedByteArray)err.getResponse().body()).getBytes());
                //Retrofit 2 way
                String string =  err.getResponse().body().toString();

                JsonObject obj = new JsonParser().parse(string).getAsJsonObject();
                JsonElement element = obj.get(identifier);

                if(element!=null){
                    String elementString = element.toString();

                    if(elementString!=null && elementString.contains("\"")){
                        return elementString.replace("\"", "");//omit all double quotes
                    }else{
                        return elementString;
                    }
                }else{
                    return "Error parsing message";
                }
            }catch (Exception e){
                e.printStackTrace();
                return "Error parsing throwable";
            }


        }else{
            return throwable.getLocalizedMessage();
        }
    }

    @NonNull public static JsonElement getJsonElementFromThrowable(@NonNull Throwable throwable, @NonNull String identifier){

        //Retrofit 2 way
        if(throwable instanceof THRetrofitException){
            THRetrofitException err = (THRetrofitException) throwable;
//            String string =  new String(((TypedByteArray)err.getResponse().getBody()).getBytes());
            String string =  err.getResponse().body().toString();
            JsonObject obj = new JsonParser().parse(string).getAsJsonObject();
            JsonElement element = obj.get(identifier);
            return element;

        }else{
            return null;
        }
    }
}
