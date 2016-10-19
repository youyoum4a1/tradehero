package com.androidth.general.utils;

import android.support.annotation.NonNull;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;

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
        if(throwable instanceof RetrofitError){
            try{
                RetrofitError err = (RetrofitError) throwable;
                String string =  new String(((TypedByteArray)err.getResponse().getBody()).getBytes());
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

        if(throwable instanceof RetrofitError){
            RetrofitError err = (RetrofitError) throwable;
            String string =  new String(((TypedByteArray)err.getResponse().getBody()).getBytes());
            JsonObject obj = new JsonParser().parse(string).getAsJsonObject();
            JsonElement element = obj.get(identifier);
            return element;

        }else{
            return null;
        }
    }
}
