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
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.persistence.user.UserProfileCache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.inject.Inject;

import retrofit.RetrofitError;

import static com.tradehero.THRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(THRobolectricTestRunner.class)
public class FollowUserAssistantTest extends FollowUserAssistantTestBase
{
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    protected THBillingInteractor billingInteractor;
    private OwnedPortfolioId applicablePortfolioId = new OwnedPortfolioId(98, 456);
    private FollowUserAssistant assistant;
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
                //noinspection unchecked,ConstantConditions
                ((THUIBillingRequest) invocation.getArguments()[0])
                        .getPurchaseReportedListener()
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
                //noinspection unchecked,ConstantConditions
                ((THUIBillingRequest) invocation.getArguments()[0])
                        .getPurchaseReportedListener()
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
        assistant = new OpenFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        userProfileCache = mock(UserProfileCache.class);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);

        assistant.launchPremiumFollow();

        verify(userProfileCache, times(1)).register(currentUserId.toUserBaseKey(), assistant);
        verify(userProfileCache, times(1)).getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Test public void callingErrorFromCacheNotifiesListener()
    {
        assistant = new FollowUserAssistant(heroId, listener, applicablePortfolioId);
        //noinspection ThrowableInstanceNeverThrown
        Throwable expected = new IllegalArgumentException();
        assistant.onErrorThrown(currentUserId.toUserBaseKey(), expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followErrorCacheNotifiesListener()
    {
        assistant = new OpenFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        userProfileCache = mock(UserProfileCache.class);
        //noinspection ThrowableInstanceNeverThrown
        final Throwable expected = new IllegalArgumentException();
        makeProfileCacheThrow(expected);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);

        assistant.launchPremiumFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followWithEnoughCCWillCallService() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        // Prepare user service
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }

    @Test public void followWithEnoughCCAndServiceFailedWillNotify() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare user service
        RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, expected);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followWithEnoughCCAndServiceSuccessWillNotify() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare user service
        UserProfileDTO expected = mockMyProfileWithCC(0d);
        prepareUserServiceForSuccessFollow(assistant, expected);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void followWithNotEnoughCCWillCallInteractor() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);
        makeBillingInteractorSaveRequest(13);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        //noinspection unchecked
        verify(billingInteractor, times(1)).run(any(THUIBillingRequest.class));
        assertThat(((OpenFollowUserAssistant) assistant).getRequestCode()).isEqualTo(13);
        assertThat(receivedRequest).isNotNull();
        assertThat(receivedRequest.getDomainToPresent()).isEqualTo(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
        assertThat(receivedRequest.getUserToPremiumFollow()).isEqualTo(heroId);
        assertThat(receivedRequest.getApplicablePortfolioId()).isEqualTo(applicablePortfolioId);
    }

    @Test public void followWithNotEnoughCCAndBoughtFailedWillNotify()
    {
        assistant = new OpenFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final BillingException billingException = mock(BillingException.class);
        makeBillingInteractorPurchaseFailed(billingException);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);

    }

    @Test public void followWithNotEnoughCCAndBoughtSuccessWillCallService() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, null, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);

        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }

    // This is very long but here to test that no listener /callback is lost in the process
    @Test public void followWithNotEnoughCCAndBoughtSuccessAndServiceFollowFailedWillNotifyListener() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);
        // Prepare user service
        RetrofitError retrofitError = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, retrofitError);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        verify(listener, times(1)).onUserFollowFailed(heroId, retrofitError);
    }

    // This is very long but here to test that no listener /callback is lost in the process
    @Test public void followWithNotEnoughCCAndBoughtSuccessAndServiceFollowSuccessWillNotifyListener() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(heroId, listener, applicablePortfolioId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.put(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);
        // Prepare user service
        UserProfileDTO myProfileAfterFollow = mockMyProfileWithCC(0d);
        prepareUserServiceForSuccessFollow(assistant, myProfileAfterFollow);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollow();
        runBgUiTasks(3);

        verify(listener, times(1)).onUserFollowSuccess(heroId, myProfileAfterFollow);
    }
}
