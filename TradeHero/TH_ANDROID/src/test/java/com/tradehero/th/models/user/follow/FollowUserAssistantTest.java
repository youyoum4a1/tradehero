package com.ayondo.academy.models.user.follow;

import android.util.Pair;
import com.ayondo.academyRobolectric;
import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.common.billing.exception.BillingException;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.billing.THBillingInteractorRx;
import com.ayondo.academy.models.user.FollowUserAssistantTestBase;
import com.ayondo.academy.models.user.OpenFollowUserAssistant;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;
import retrofit.RetrofitError;
import rx.Observable;

import static com.ayondo.academyRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class FollowUserAssistantTest extends FollowUserAssistantTestBase
{
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    protected THBillingInteractorRx billingInteractor;
    private OwnedPortfolioId applicablePortfolioId = new OwnedPortfolioId(98, 456);
    private FollowUserAssistant assistant;

    @Before @Override public void setUp()
    {
        super.setUp();
        THRobolectric.setupActivity(DashboardActivityExtended.class).inject(this);
        currentUserId.set(98);
        billingInteractor = mock(THBillingInteractorRx.class);
    }

    @After public void tearDown()
    {
        userProfileCache.invalidateAll();
        billingInteractor = null;
        applicablePortfolioId = null;
        assistant = null;
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
            @Override public Observable<Pair<UserBaseKey, UserProfileDTO>> answer(InvocationOnMock invocation) throws Throwable
            {
                return Observable.error(expected);
            }
        }).when(userProfileCache).get(currentUserId.toUserBaseKey());
    }

    protected void makeBillingInteractorPurchaseSuccess(final UserProfileDTO myProfileAfterPurchase)
    {
        //noinspection unchecked
        //when(billingInteractor.run(any(THUIBillingRequest.class)))
        //        .then(createCCPurchaseSuccessAnswer(myProfileAfterPurchase));
    }

    protected Answer<Object> createCCPurchaseSuccessAnswer(final UserProfileDTO myProfileAfterPurchase)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Integer requestCode = 22;
                //noinspection unchecked,ConstantConditions
                //((THUIBillingRequest) invocation.getArguments()[0])
                //        .getPurchaseReportedListener()
                //        .onPurchaseReported(
                //                requestCode,
                //                mock(ProductPurchase.class),
                //                myProfileAfterPurchase);
                return requestCode;
            }
        };
    }

    protected void makeBillingInteractorPurchaseFailed(final BillingException billingException)
    {
        //noinspection unchecked
        //when(billingInteractor.run(any(THUIBillingRequest.class)))
        //        .then(createCCPurchaseFailedAnswer(billingException));
    }

    protected Answer<Object> createCCPurchaseFailedAnswer(final BillingException billingException)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Integer requestCode = 22;
                //noinspection unchecked,ConstantConditions
                //((THUIBillingRequest) invocation.getArguments()[0])
                //        .getPurchaseReportedListener()
                //        .onPurchaseReportFailed(
                //                requestCode,
                //                mock(ProductPurchase.class),
                //                billingException);
                return requestCode;
            }
        };
    }

    protected void makeBillingInteractorSaveRequest(final int requestCode)
    {
        //noinspection unchecked
        //when(billingInteractor.run(any(THUIBillingRequest.class)))
        //        .then(new Answer<Object>()
        //        {
        //            @Override public Object answer(InvocationOnMock invocation) throws Throwable
        //            {
        //                receivedRequest = (THUIBillingRequest) invocation.getArguments()[0];
        //                return requestCode;
        //            }
        //        });
    }

    @Test public void followCallsCache()
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        userProfileCache = mock(UserProfileCacheRx.class);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);

        assistant.launchPremiumFollowRx().subscribe();

        verify(userProfileCache, times(1)).get(currentUserId.toUserBaseKey());
    }

    @Test(expected = IllegalArgumentException.class)
    public void followErrorCacheNotifiesListener()
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        userProfileCache = mock(UserProfileCacheRx.class);
        //noinspection ThrowableInstanceNeverThrown
        final Throwable expected = new IllegalArgumentException();
        makeProfileCacheThrow(expected);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);

        assistant.launchPremiumFollowRx().subscribe();
    }

    @Test public void followWithEnoughCCWillCallService() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myProfile);
        // Prepare user service
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx();
        runBgUiTasks(3);

        verify(userServiceWrapper, times(1)).followRx(heroId);
    }

    @Test(expected = RetrofitError.class)
    public void followWithEnoughCCAndServiceFailedWillNotify() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare user service
        RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, expected);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx().subscribe();
        runBgUiTasks(3);
    }

    @Test public void followWithEnoughCCAndServiceSuccessWillNotify() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(1d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare user service
        UserProfileDTO expected = mockMyProfileWithCC(0d);
        prepareUserServiceForSuccessFollow(assistant, expected);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx();
        runBgUiTasks(3);

        //verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void followWithNotEnoughCCWillCallInteractor() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myProfile = mockMyProfileWithCC(0d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);
        makeBillingInteractorSaveRequest(13);

        assistant.launchPremiumFollowRx();
        runBgUiTasks(3);

        //noinspection unchecked
        //verify(billingInteractor, times(1)).run(any(THUIBillingRequest.class));
        assertThat(((OpenFollowUserAssistant) assistant).getRequestCode()).isEqualTo(13);
        //assertThat(receivedRequest).isNotNull();
        //assertThat(receivedRequest.getDomainToPresent()).isEqualTo(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
        //assertThat(receivedRequest.getUserToPremiumFollow()).isEqualTo(heroId);
        //assertThat(receivedRequest.getApplicablePortfolioId()).isEqualTo(applicablePortfolioId);
    }

    @Test public void followWithNotEnoughCCAndBoughtFailedWillNotify()
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final BillingException billingException = mock(BillingException.class);
        makeBillingInteractorPurchaseFailed(billingException);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);

    }

    @Test public void followWithNotEnoughCCAndBoughtSuccessWillCallService() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);

        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx();
        runBgUiTasks(3);

        verify(userServiceWrapper, times(1)).followRx(heroId);
    }

    // This is very long but here to test that no listener /callback is lost in the process
    @Test(expected = RetrofitError.class)
    public void followWithNotEnoughCCAndBoughtSuccessAndServiceFollowFailedWillNotifyListener() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);
        // Prepare user service
        RetrofitError retrofitError = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, retrofitError);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx().subscribe();
        runBgUiTasks(3);
    }

    // This is very long but here to test that no listener /callback is lost in the process
    @Test public void followWithNotEnoughCCAndBoughtSuccessAndServiceFollowSuccessWillNotifyListener() throws InterruptedException
    {
        assistant = new OpenFollowUserAssistant(THApp.context(), heroId);
        // Prepare cache
        UserProfileDTO myInitialProfile = mockMyProfileWithCC(0d);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), myInitialProfile);
        ((OpenFollowUserAssistant) assistant).setUserProfileCache(userProfileCache);
        // Prepare interactor
        final UserProfileDTO myProfileAfterPurchase = mockMyProfileWithCC(1d);
        makeBillingInteractorPurchaseSuccess(myProfileAfterPurchase);
        ((OpenFollowUserAssistant) assistant).setBillingInteractor(billingInteractor);
        // Prepare user service
        UserProfileDTO myProfileAfterFollow = mockMyProfileWithCC(0d);
        prepareUserServiceForSuccessFollow(assistant, myProfileAfterFollow);
        ((OpenFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx();
        runBgUiTasks(3);

        //verify(listener, times(1)).onUserFollowSuccess(heroId, myProfileAfterFollow);
    }
}
