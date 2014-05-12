package com.tradehero.th.network.service;

import com.tradehero.th.models.translation.TranslationResult;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

public interface TranslationServiceAsync
{
    @GET("/v2/Http.svc/Translate") void requestForTranslation(
            @Header("Authorization") String authorization,
            @Query("from") String from,
            @Query("to") String to,
            @Query("contentType") String contentType,
            @Query("text") String text,
            Callback<TranslationResult> callback);
}

//        Get access_token

//        POST https://datamarket.accesscontrol.windows.net/v2/OAuth2-13 with body params
//        scope=http://api.microsofttranslator.com
//        grant_type=client_credentials
//        client_id=<BING_CLIENT_ID>
//                client_secret=<BING_SECRET>

//Get access_token return:
//        {
//            "token_type": "http://schemas.xmlsoap.org/ws/2009/11/swt-token-profile-1.0",
//                "access_token": "http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fnameidentifier=12375812376439443957334&http%3a%2f%2fschemas.microsoft.com%2faccesscontrolservice%2f2010%2f07%2fclaims%2fidentityprovider=https%3a%2f%2fdatamarket.accesscontrol.windows.net%2f&Audience=http%3a%2f%2fapi.microsofttranslator.com&ExpiresOn=1393984740&Issuer=https%3a%2f%2fdatamarket.accesscontrol.windows.net%2f&HMACSHA256=mCOgnnCcfGrkmVFcKBewq9UDEPxKVSqgNtmCn%2bF%2bQFM%3d",
//                "expires_in": "599",
//                "scope": "http://api.microsofttranslator.com"
//        }

//--------------------------------
//        Get translation:

//        GET http://api.microsofttranslator.com/v2/Http.svc/Translate with header
//        Authorization: Bearer <access_token>
//        and params
//        from=<from_language_code>
//                to=<to_language_code>
//                contentType="text/plain"
//        text=<text_to_translate>

//translation return:
//        <string xmlns="http://schemas.microsoft.com/2003/10/Serialization/">Millet, a understancultuship between man and nature.
//        </string>
//--------------------------------

//        /Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/bin/java -Didea.launcher.port=7538 "-Didea.launcher.bin.path=/Applications/IntelliJ IDEA 13 CE.app/bin" -Dfile.encoding=UTF-8 -classpath "/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/javafx-doclet.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/lib/tools.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/htmlconverter.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Users/tradehero/Documents/tradehero/github/retrofit/retrofit-samples/github-client/target/classes:/Users/tradehero/.m2/repository/com/squareup/retrofit/retrofit/1.4.1/retrofit-1.4.1.jar:/Users/tradehero/.m2/repository/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar:/Users/tradehero/.m2/repository/com/netflix/rxjava/rxjava-core/0.17.0-RC6/rxjava-core-0.17.0-RC6.jar:/Applications/IntelliJ IDEA 13 CE.app/lib/idea_rt.jar" com.intellij.rt.execution.application.AppMain com.example.retrofit.Test
//        token:[token_type:http://schemas.xmlsoap.org/ws/2009/11/swt-token-profile-1.0;access_token:http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fnameidentifier=12375812376439443957334&http%3a%2f%2fschemas.microsoft.com%2faccesscontrolservice%2f2010%2f07%2fclaims%2fidentityprovider=https%3a%2f%2fdatamarket.accesscontrol.windows.net%2f&Audience=http%3a%2f%2fapi.microsofttranslator.com&ExpiresOn=1393925206&Issuer=https%3a%2f%2fdatamarket.accesscontrol.windows.net%2f&HMACSHA256=dpzLa%2bikoSNL1extgkMOyEsb5iNQScilPL8sJ7Va3fY%3d;expires_in:599;scope:http://api.microsofttranslator.com]
//        decoded token http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier=12375812376439443957334&http://schemas.microsoft.com/accesscontrolservice/2010/07/claims/identityprovider=https://datamarket.accesscontrol.windows.net/&Audience=http://api.microsofttranslator.com&ExpiresOn=1393925206&Issuer=https://datamarket.accesscontrol.windows.net/&HMACSHA256=dpzLa+ikoSNL1extgkMOyEsb5iNQScilPL8sJ7Va3fY=
//        Process finished with exit code 0

//    }
//}
