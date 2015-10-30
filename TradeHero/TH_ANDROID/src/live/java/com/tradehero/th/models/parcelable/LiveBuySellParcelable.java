package com.tradehero.th.models.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import com.tradehero.th.api.security.SecurityId;

public class LiveBuySellParcelable implements Parcelable
{
    private SecurityId securityId;
    private Integer shares;

    public LiveBuySellParcelable(SecurityId securityId, Integer shares)
    {
        this.securityId = securityId;
        this.shares = shares;
    }

    public Integer getShares()
    {
        return shares;
    }

    public SecurityId getSecurityId()
    {
        return securityId;
    }

    @Override public String toString()
    {
        return "LiveBuySellParcelable[ " +
                securityId.toString() +
                "shares= " + shares.toString() +
                "]";
    }

    @Override public int describeContents()
    {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(this.securityId, 0);
        dest.writeValue(this.shares);
    }

    private LiveBuySellParcelable(Parcel in)
    {
        this.securityId = in.readParcelable(SecurityId.class.getClassLoader());
        this.shares = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<LiveBuySellParcelable> CREATOR = new Creator<LiveBuySellParcelable>()
    {
        public LiveBuySellParcelable createFromParcel(Parcel source)
        {
            return new LiveBuySellParcelable(source);
        }

        public LiveBuySellParcelable[] newArray(int size)
        {
            return new LiveBuySellParcelable[size];
        }
    };
}
