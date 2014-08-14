package com.tradehero.th.models.user;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.RetrofitError;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(THRobolectricTestRunner.class)
public class PremiumFollowUserAssistantTest extends FollowUserAssistantTestBase
{
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    protected THBillingInteractor billingInteractor;
    private OwnedPortfolioId applicablePortfolioId = new OwnedPortfolioId(98, 456);
    private PremiumFollowUserAssistant assistant;
    private THUIBillingRequest receivedRequest;

    @Before @Override public void setUp()
    {
        super.setUp();
        currentUserId.set(98);
        billingInteractor = mock(THBillingInteractor.class);
    }

    @After public void tearDown()
    {
        if (assistant != null)
        {
            assistant.setUserFollowedListener(null);
        }
        userProfileCache.invalidateAll();
        billingInteractor = null;
        applicablePortfolioId = null;
        assistant = null;
        receivedRequest = null;
    }

    protected UserProfileDTO mockMyProfileWithCC(double credits)
    {
        UserProfileDTO mockedProfile = mock(UserProfileDTO.class);
        mockedProfile.id = currentUserId.get();
        mockedProfile.ccBalance = credits;
        return mockedProfile;
    }

    protected void makeProfileCacheThrow(final Throwable expected)
    {
        doAnswer(new Answer()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assistant.onErrorThrown(heroId, expected);
                return null;
            }
        }).when(userProfileCache).getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void makeBillingInteractorPurchaseSuccess(final UserProfileDTO myProfileAfterPurchase)
    {
        //noinspection unchecked
        when(billingInteractor.run(any(THUIBillingRequest.class)))
                .then(createCCPurchaseSuccessAnswer(myProfileAfterPurchase));
    }

    protected Answer<Object> createCCPurchaseSuccessAnswer(final UserProfileDTO myProfileAfterPurchase)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Integer requestCode = 22;
                //noinspection unchecked
                ((THUIBillingRequest) invocation.getArguments()[0])
                        .purchaseReportedListener
                        .onPurchaseReported(
                                requestCode,
                                mock(ProductPurchase.class),
                                myProfileAfterPurchase);
                return requestCode;
            }
        };
    }

    protected void makeBillingInteractorPurchaseFailed(final BillingException billingException)
    {
        //noinspection unchecked
        when(billingInteractor.run(any(THUIBillingRequest.class)))
                .then(createCCPurchaseFailedAnswer(billingException));
    }

    protected Answer<Object> createCCPurchaseFailedAnswer(final BillingException billingException)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Integer requestCode = 22;
                //noinspection unchecked
                ((THUIBillingRequest) invocation.getArguments()[0])
                        .purchaseReportedListener
                        .onPurchaseReportFailed(
                                requestCode,
                                mock(ProductPurchase.class),
                                billingException);
                return requestCode;
            }
        };
    }

    protected void makeBillingInteractorSaveRequest(final int requestCode)
    {
        //noinspection unchecked
        when(billingInteractor.run(any(THUIBillingRequest.class)))
                .then(new Answer<Object>()
                {
                    @Override public Object answer(InvocationOnMock invocation) throws Throwable
                    {
                        receivedRequest = (THUIBillingRequest) invocation.getArguments()[0];
                        return requestCode;
                    }
                });
    }

    @Test public void followCallsCache()
    {
        assistant = new PremiumFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        userProfileCache = mock(UserProfileCache.class);
        assistant.userProfileCache = userProfileCache;

        assistant.launchFollow();

        verify(userProfileCache, times(1)).register(currentUserId.toUserBaseKey(), assistant);
        verify(userProfileCache, times(1)).getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Test public void callingErrorFromCacheNotifiesListener()
    {
        assistant = new PremiumFollowUserAssistant(heroId, listener, applicablePortfolioId);
        //noinspection ThrowableInstanceNeverThrown
        Throwable expected = new IllegalArgumentException();
        assistant.onErrorThrown(currentUserId.toUserBaseKey(), expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followErrorCacheNotifiesListener()
    {
        assistant = new PremiumFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        userProfileCache = mock(UserProfileCache.class);
        //noinspection ThrowableInstanceNeverThrown
        final Throwable expected = new IllegalArgumentException();
        makeProfileCacheThrow(expected);
        assistant.userProfileCache = userProfileCache;

        assistant.launchFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followWithEnoughCCWillCallService()
    {
        assistant = new PremiumFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        assistant.userProfileCache = userProfileCache;
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        // Prepare user service
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();
        runBgUiTasks(10);

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }

    @Test public void followWithEnoughCCAndServiceFailedWillNotify()
    {
        assistant = new PremiumFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare user service
        RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, expected);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();
        runBgUiTasks(10);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followWithEnoughCCAndServiceSuccessWillNotify()
    {
        assistant = new PremiumFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare user service
        UserProfileDTO expected = mockMyProfileWithCC(0d);
        prepareUserServiceForSuccessFollow(assistant, expected);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();
        runBgUiTasks(10);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void followWithNotEnoughCCWillCallInteractor()
    {
        assistant = new PremiumFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare interactor
        assistant.billingInteractor = billingInteractor;
        makeBillingInteractorSaveRequest(13);

        assistant.launchFollow();
        runBgUiTasks(10);

        //noinspection unchecked
        verify(billingInteractor, times(1)).run(any(THUIBillingRequest.class));
        assertThat(assistant.requestCode).isEqualTo(13);
        assertThat(receivedRequest).isNotNull();
        assertThat(receivedRequest.domainToPresent).isEqualTo(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
        assertThat(receivedRequest.userToFollow).isEqualTo(heroId);
        assertThat(receivedRequest.applicablePortfolioId).isEqualTo(applicablePortfolioId);
    }

    @Test public void followWithNotEnoughCCAndBoughtFailedWillNotify()
    {
        assistant = new PremiumFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare interactor
        final BillingException billingException = mock(BillingException.class);
        makeBillingInteractorPurchaseFailed(billingException);
        assistant.billingInteractor = billingInteractor;

    }

    @Test public void followWithNotEnoughCCAndBoughtSuccessWillCallService()
    {
        assistant = new PremiumFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        assistant.billingInteractor = billingInteractor;

        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();
        runBgUiTasks(10);

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }

    // This is very long but here to test that no listener /callback is lost in the process
    @Test public void followWithNotEnoughCCAndBoughtSuccessAndServiceFollowFailedWillNotifyListener()
    {
        assistant = new PremiumFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        assistant.billingInteractor = billingInteractor;
        // Prepare user service
        RetrofitError retrofitError = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, retrofitError);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();
        runBgUiTasks(10);

        verify(listener, times(1)).onUserFollowFailed(heroId, retrofitError);
    }

    // This is very long but here to test that no listener /callback is lost in the process
    @Test public void followWithNotEnoughCCAndBoughtSuccessAndServiceFollowSuccessWillNotifyListener()
    {
        assistant = new PremiumFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        assistant.userProfileCache = userProfileCache;
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        assistant.billingInteractor = billingInteractor;
        // Prepare user service
        UserProfileDTO myProfileAfterFollow = mockMyProfileWithCC(0d);
        prepareUserServiceForSuccessFollow(assistant, myProfileAfterFollow);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();
        runBgUiTasks(10);

        verify(listener, times(1)).onUserFollowSuccess(heroId, myProfileAfterFollow);
    }
}
