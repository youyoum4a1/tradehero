package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.fragments.live.CountrySpinnerAdapter;
import com.tradehero.th.models.fastfill.Gender;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.widget.OnTextChangeEvent;

public class KYCAyondoFormFactory
{
    @NonNull public static KYCAyondoForm fromTitleEvent(@NonNull OnSelectedEvent titleEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (titleEvent instanceof OnItemSelectedEvent)
        {
            Gender newGender = ((GenderDTO) titleEvent.parent.getItemAtPosition(
                    ((OnItemSelectedEvent) titleEvent).position)).gender;
            created.setGender(newGender);
        }
        return created;
    }

    @Deprecated
    @NonNull public static KYCAyondoForm fromFullNameEvent(@NonNull OnTextChangeEvent fullNameEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setFullName(fullNameEvent.text().toString());
        return created;
    }

    @NonNull public static KYCAyondoForm fromFirstNameEvent(OnTextChangeEvent firstNameEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setFirstName(firstNameEvent.text().toString());
        return created;
    }

    @NonNull public static KYCAyondoForm fromLastNameEvent(@NonNull OnTextChangeEvent lastNameEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setLastName(lastNameEvent.text().toString());
        return created;
    }

    @NonNull public static KYCAyondoForm fromEmailEvent(@NonNull OnTextChangeEvent emailEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setEmail(emailEvent.text().toString());
        return created;
    }

    @NonNull public static KYCAyondoForm fromNationalityEvent(@NonNull OnSelectedEvent nationalityEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (nationalityEvent instanceof OnItemSelectedEvent)
        {
            CountryCode newNationality =
                    CountryCode.getByCode(
                            ((CountrySpinnerAdapter.DTO) nationalityEvent.parent.getItemAtPosition(
                                    ((OnItemSelectedEvent) nationalityEvent).position)).country.name());
            //noinspection ConstantConditions
            created.setNationality(newNationality);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromResidencyEvent(@NonNull OnSelectedEvent residencyEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (residencyEvent instanceof OnItemSelectedEvent)
        {
            CountryCode newResidency =
                    CountryCode.getByCode(
                            ((CountrySpinnerAdapter.DTO) residencyEvent.parent.getItemAtPosition(
                                    ((OnItemSelectedEvent) residencyEvent).position)).country.name());
            created.setResidency(newResidency);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromDobEvent(@NonNull OnTextChangeEvent dobEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setDob(dobEvent.text().toString());
        return created;
    }

    @NonNull public static KYCAyondoForm fromAnnualIncomeRangeEvent(@NonNull OnSelectedEvent annualIncomeRangeEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (annualIncomeRangeEvent instanceof OnItemSelectedEvent)
        {
            created.setAnnualIncomeRange(
                    ((AnnualIncomeDTO) annualIncomeRangeEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) annualIncomeRangeEvent).position)).annualIncomeRange);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromNetWorthRangeEvent(@NonNull OnSelectedEvent netWorthRangeEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (netWorthRangeEvent instanceof OnItemSelectedEvent)
        {
            created.setNetWorthRange(
                    ((NetWorthDTO) netWorthRangeEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) netWorthRangeEvent).position)).netWorthRange);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromPercentNetWorthRangeEvent(@NonNull OnSelectedEvent percentNetWorthRangeEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (percentNetWorthRangeEvent instanceof OnItemSelectedEvent)
        {
            created.setPercentNetWorthForInvestmentRange(
                    ((PercentNetWorthDTO) percentNetWorthRangeEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) percentNetWorthRangeEvent).position)).netWorthForInvestmentRange);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromEmploymentStatusEvent(@NonNull OnSelectedEvent employmentStatusEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (employmentStatusEvent instanceof OnItemSelectedEvent)
        {
            //noispection ConstantConditions
            created.setEmploymentStatus(
                    ((EmploymentStatusDTO) employmentStatusEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) employmentStatusEvent).position)).employmentStatus);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromEmployerRegulatedEvent(@NonNull OnCheckedChangeEvent employerRegulatedEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setEmployerRegulatedFinancial(employerRegulatedEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromWorkedInFinance1YearEvent(@NonNull OnCheckedChangeEvent workedInFinanceEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setWorkedInFinance1Year(workedInFinanceEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromAttendedSeminarAyondoEvent(@NonNull OnCheckedChangeEvent attendedSeminarAyondoEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setAttendedSeminarAyondo(attendedSeminarAyondoEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromHaveOtherQualificationEvent(@NonNull OnCheckedChangeEvent haveOtherQualificationEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setHaveOtherQualification(haveOtherQualificationEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromProductsTradedEvent(@NonNull OnCheckedChangeEvent tradedSharesEvent,
            @NonNull OnCheckedChangeEvent tradedOtcDerivativesEvent,
            @NonNull OnCheckedChangeEvent tradedExchangeEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setTradedSharesBonds(tradedSharesEvent.value());
        created.setTradedOtcDerivative(tradedOtcDerivativesEvent.value());
        created.setTradedEtc(tradedExchangeEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromTradingPerQuarterEvent(@NonNull OnSelectedEvent tradingPerQuarterEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (tradingPerQuarterEvent instanceof OnItemSelectedEvent)
        {
            created.setTradingPerQuarter(
                    ((TradingPerQuarterDTO) tradingPerQuarterEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) tradingPerQuarterEvent).position)).tradingPerQuarter);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromIdentityDocumentTypeEvent(@NonNull OnSelectedEvent identityDocumentTypeEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (identityDocumentTypeEvent instanceof OnItemSelectedEvent)
        {
            created.setIdentityDocumentType(
                    ((IdentityDocumentDTO) identityDocumentTypeEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) identityDocumentTypeEvent).position)).identityScannedDocumentType);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromResidenceDocumentTypeEvent(@NonNull OnSelectedEvent residenceDocumentTypeEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (residenceDocumentTypeEvent instanceof OnItemSelectedEvent)
        {
            created.setResidenceDocumentType(
                    ((ResidenceDocumentDTO) residenceDocumentTypeEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) residenceDocumentTypeEvent).position)).residenceScannedDocumentType);
        }
        return created;
    }

    @NonNull public static KYCAyondoForm fromAgreeTermsConditionsEvent(@NonNull OnCheckedChangeEvent termsConditionsEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setAgreeTermsConditions(termsConditionsEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromAgreeRiskWarningEvent(@NonNull OnCheckedChangeEvent riskWarningEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setAgreeRisksWarnings(riskWarningEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromAgreeDataSharingEvent(@NonNull OnCheckedChangeEvent dataSharingEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setAgreeDataSharing(dataSharingEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromSubscribeOffers(@NonNull OnCheckedChangeEvent subscribeOffersEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setSubscribeOffers(subscribeOffersEvent.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromSubscribeTradeNotifications(@NonNull OnCheckedChangeEvent subscribeTradeNotifications)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        created.setSubscribeTradeNotifications(subscribeTradeNotifications.value());
        return created;
    }

    @NonNull public static KYCAyondoForm fromCurrencySpinnerEvent(@NonNull OnSelectedEvent currencyEvent)
    {
        KYCAyondoForm created = new KYCAyondoForm();
        if (currencyEvent instanceof OnItemSelectedEvent)
        {
            //noispection ConstantConditions
            created.setCurrency(
                    ((CurrencyDTO) currencyEvent.parent.getItemAtPosition(
                            ((OnItemSelectedEvent) currencyEvent).position)).currency);
        }
        return created;
    }
}
