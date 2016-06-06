package com.androidth.general.common.facebook;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class FacebookGraphPicture
{
    private static final String KEY_URL = "url";
    private static final String KEY_IS_SILHOUETTE = "is_silhouette";
    private static final String KEY_WIDTH = "width";
    private static final String KEY_HEIGHT = "height";

    @Nullable public final String url;
    @Nullable public final Boolean isSilhouette;
    @Nullable public final Integer width;
    @Nullable public final Integer height;

    //<editor-fold desc="Constructors">
    public FacebookGraphPicture(JSONObject pictureObject) throws JSONException
    {
        url = (String) pictureObject.get(KEY_URL);
        isSilhouette = (Boolean) pictureObject.get(KEY_IS_SILHOUETTE);
        width = (Integer) pictureObject.get(KEY_WIDTH);
        height = (Integer) pictureObject.get(KEY_HEIGHT);
        Timber.d("Facebook " + url);
    }
    //</editor-fold>
}
