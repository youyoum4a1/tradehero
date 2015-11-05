package com.tradehero.th.models.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import com.tradehero.th.api.security.SecurityId;

public class LiveTransactionParcelable implements Parcelable
{
    private SecurityId securityId;
    private Integer shares;
    private boolean isTransactionBuy;

    public LiveTransactionParcelable(SecurityId securityId, Integer shares, boolean isTransactionBuy)
    {
        this.securityId = securityId;
        this.shares = shares;
        this.isTransactionBuy = isTransactionBuy;
    }

    public SecurityId getSecurityId()
    {
        return securityId;
    }

    public Integer getShares()
    {
        return shares;
    }

    public boolean isTransactionBuy()
    {
        return isTransactionBuy;
    }

    @Override public int describeContents()
    {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeParcelable(this.securityId, 0);
        dest.writeValue(this.shares);
        dest.writeByte(isTransactionBuy ? (byte) 1 : (byte) 0);
    }

    private LiveTransactionParcelable(Parcel in)
    {
        this.securityId = in.readParcelable(SecurityId.class.getClassLoader());
        this.shares = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isTransactionBuy = in.readByte() != 0;
    }

    public static final Parcelable.Creator<LiveTransactionParcelable> CREATOR = new Parcelable.Creator<LiveTransactionParcelable>()
    {
        public LiveTransactionParcelable createFromParcel(Parcel source)
        {
            return new LiveTransactionParcelable(source);
        }

        public LiveTransactionParcelable[] newArray(int size)
        {
            return new LiveTransactionParcelable[size];
        }
    };
}
