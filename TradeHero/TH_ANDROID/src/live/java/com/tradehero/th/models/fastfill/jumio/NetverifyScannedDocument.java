package com.tradehero.th.models.fastfill.jumio;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.jumio.netverify.sdk.NetverifyDocumentData;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.models.fastfill.IdentityScannedDocumentType;
import com.tradehero.th.models.fastfill.ScannedDocument;
import java.util.Date;

public class NetverifyScannedDocument implements ScannedDocument
{
    @NonNull private final NetverifyScanReference scanReference;
    @NonNull private final NetverifyDocumentData data;

    public NetverifyScannedDocument(@NonNull NetverifyScanReference scanReference, @NonNull NetverifyDocumentData data)
    {
        this.scanReference = scanReference;
        this.data = data;
    }

    @NonNull @Override public NetverifyScanReference getScanReference()
    {
        return scanReference;
    }

    @Override @Nullable public String getFirstName()
    {
        return data.getFirstName();
    }

    @Override @Nullable public String getMiddleName()
    {
        return data.getMiddleName();
    }

    @Override @Nullable public String getLastName()
    {
        return data.getLastName();
    }

    @Override @Nullable public String getFullName()
    {
        String firstName = getFirstName();
        String lastName = getLastName();
        if (firstName == null)
        {
            return lastName;
        }
        else if (lastName == null)
        {
            return firstName;
        }
        else
        {
            return TextUtils.concat(firstName, " ", lastName).toString().trim();
        }
    }

    @Override @Nullable public Gender getGender()
    {
        switch (data.getGender())
        {
            case M:
                return Gender.MALE;
            case F:
                return Gender.FEMALE;
            default:
                return null;
        }
    }

    @Override @Nullable public Date getDob()
    {
        return data.getDob();
    }

    @Nullable @Override public IdentityScannedDocumentType getSelectedDocumentType()
    {
        switch (data.getSelectedDocumentType())
        {
            case PASSPORT:
                return IdentityScannedDocumentType.PASSPORT;
            case DRIVER_LICENSE:
                return IdentityScannedDocumentType.DRIVER_LICENSE;
            case IDENTITY_CARD:
                return IdentityScannedDocumentType.IDENTITY_CARD;
            default:
                return null;
        }
    }

    @Override @Nullable public String getPersonalNumber()
    {
        return data.getPersonalNumber();
    }

    @Override @Nullable public String getIdNumber()
    {
        return data.getIdNumber();
    }

    @Override @Nullable public CountryCode getSelectedCountry()
    {
        return CountryCode.getByCode(data.getSelectedCountry());
    }

    @Override @Nullable public CountryCode getIssuingCountry()
    {
        return CountryCode.getByCode(data.getIssuingCountry());
    }

    @Override @Nullable public CountryCode getOriginatingCountry()
    {
        return CountryCode.getByCode(data.getOriginatingCountry());
    }

    @Override @Nullable public Date getIssuingDate()
    {
        return data.getIssuingDate();
    }

    @Override @Nullable public Date getExpiryDate()
    {
        return data.getExpiryDate();
    }

    //@Override @Nullable public NetverifyMrzData getMrzData()
    //{
    //    return data.getMrzData();
    //}

    @Override public int getNameDistance()
    {
        return data.getNameDistance();
    }

    @Override @Nullable public String getOptionalData1()
    {
        return data.getOptionalData1();
    }

    @Override @Nullable public String getOptionalData2()
    {
        return data.getOptionalData2();
    }

    @Override @Nullable public String getStreet()
    {
        return data.getStreet();
    }

    @Override @Nullable public String getCity()
    {
        return data.getCity();
    }

    @Override @Nullable public String getPostalCode()
    {
        return data.getPostalCode();
    }

    @Override @Nullable public String getState()
    {
        return data.getState();
    }

    @Override public boolean isNameMatch()
    {
        return data.isNameMatch();
    }

    @Override @Nullable public Boolean getLivenessDetected()
    {
        return data.getLivenessDetected();
    }
}
