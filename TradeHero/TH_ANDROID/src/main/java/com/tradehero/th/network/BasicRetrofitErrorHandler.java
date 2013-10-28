package com.tradehero.th.network;

import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/2/13 Time: 5:38 PM To change this template use File | Settings | File Templates. */
public class BasicRetrofitErrorHandler
{
    public static String getResponseContent(RetrofitError retrofitError) throws IOException
    {
        StringWriter writer = new StringWriter();
        IOUtils.copy(retrofitError.getResponse().getBody().in(), writer, "UTF-8");
        return writer.toString();
    }

    public static void handle(RetrofitError retrofitError)
    {
        if (retrofitError.isNetworkError())
        {
            notifyNetworkError(retrofitError);
        }
    }

    public static void notifyNetworkError(RetrofitError retrofitError)
    {
        THToast.show(R.string.network_error);
    }
}
