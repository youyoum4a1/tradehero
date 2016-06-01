package com.ayondo.academy.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.LayoutInflater;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.R;
import com.ayondo.academy.activities.DashboardActivity;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.level.LevelDefDTO;
import com.ayondo.academy.api.level.LevelDefDTOList;
import com.ayondo.academy.api.level.key.LevelDefListId;
import com.ayondo.academy.persistence.level.LevelDefListCacheRx;
import java.util.List;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserLevelProgressBarTest
{
    private UserLevelProgressBar userLevelProgressBar;
    @Inject LevelDefListCacheRx levelDefListCache;

    @Before
    public void setUp()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);

        LevelDefDTO mockLevel1 = new LevelDefDTO();
        mockLevel1.id = 1;
        mockLevel1.level = 1;
        mockLevel1.xpFrom = 0;
        mockLevel1.xpTo = 1000;

        LevelDefDTO mockLevel2 = new LevelDefDTO();
        mockLevel2.id = 2;
        mockLevel2.level = 2;
        mockLevel2.xpFrom = 1001;
        mockLevel2.xpTo = 2000;

        LevelDefDTO mockLevel3 = new LevelDefDTO();
        mockLevel3.id = 3;
        mockLevel3.level = 3;
        mockLevel3.xpFrom = 2001;
        mockLevel3.xpTo = 3000;

        LevelDefDTO mockLevel4 = new LevelDefDTO();
        mockLevel4.id = 4;
        mockLevel4.level = 4;
        mockLevel4.xpFrom = 3001;
        mockLevel4.xpTo = 999999;

        LevelDefDTOList levelDefDTOList = new LevelDefDTOList();
        levelDefDTOList.add(mockLevel1);
        levelDefDTOList.add(mockLevel4);
        levelDefDTOList.add(mockLevel3);
        levelDefDTOList.add(mockLevel2);

        levelDefListCache.onNext(new LevelDefListId(), levelDefDTOList);

        userLevelProgressBar = (UserLevelProgressBar) LayoutInflater.from(activity).inflate(R.layout.user_level_progressbar, null, false);
        userLevelProgressBar.setLevelDefDTOList(levelDefDTOList);
    }

    @After
    public void tearDown()
    {
        userLevelProgressBar = null;
    }

    @Test(expected = RuntimeException.class) public void testShouldCrashWhenListIsNotSet()
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);
        userLevelProgressBar = (UserLevelProgressBar) LayoutInflater.from(activity).inflate(R.layout.user_level_progressbar, null, false);
        userLevelProgressBar.startsWith(200);
    }

    @Test(expected = RuntimeException.class) public void testShouldCrashWhenNotInitiated()
    {
        userLevelProgressBar.increment(200);
    }

    @Test public void shouldDisplayCorrectLevel()
    {
        userLevelProgressBar.startsWith(500);
        assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("1");
        assertThat(userLevelProgressBar.getNextLevel()).isEqualTo("2");
    }

    @Test public void testShouldNotLevelUp()
    {
        userLevelProgressBar.startsWith(400);
        assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("1");

        //Robolectric is unable to use ofPropertyValues method of ObjectAnimator...
        //userLevelProgressBar.increment(200);

        List<Animator> animators = userLevelProgressBar.getAnimatorQueue(200);

        assertThat(animators).isNotNull();

        assertThat(animators.size()).isEqualTo(1);

        assertThat(animators.get(0).getListeners()).isNull();
    }

    @Test public void testShouldLevelUp()
    {
        //TODO when we know how to Run animator test with Robolectric
        //userLevelProgressBar.startsWith(400);
        //assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("1");
        //
        ////Robolectric is unable to use ofPropertyValues method of ObjectAnimator...
        ////userLevelProgressBar.increment(200);
        //
        //List<Animator> animators = userLevelProgressBar.getAnimatorQueue(840);
        //
        //assertThat(animators).isNotNull();
        //
        //assertThat(animators.size()).isEqualTo(2);
        //
        //ObjectAnimator a = (ObjectAnimator) animators.get(0);
        //
        //assertThat(a.getListeners()).isNotNull();
        //assertThat(a.getListeners().size()).isEqualTo(1);
        //
        //assertThat(animators.get(1).getListeners()).isNull();
    }

    @Test public void testShouldAdvanceWithCorrectValuesWhenLevelis1()
    {
        userLevelProgressBar.startsWith(400);
        assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("1");

        List<Animator> animators = userLevelProgressBar.getAnimatorQueue(200);
        assertThat(animators).isNotNull();

        ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(0);

        PropertyValuesHolder[] propertyValuesHolders = objectAnimator.getValues();

        assertThat(propertyValuesHolders).isNotNull();
        assertThat(propertyValuesHolders.length).isEqualTo(3);

        PropertyValuesHolder p0Progress = propertyValuesHolders[0];
        PropertyValuesHolder p0Secondary = propertyValuesHolders[1];
        PropertyValuesHolder p0Max = propertyValuesHolders[2];

        assertThat(p0Progress).isNotNull();
        assertThat(p0Progress.getPropertyName()).isEqualTo("progress");
        assertThat(p0Progress.toString()).isEqualTo(getPropertyValuesToString("progress", "400", "400"));

        assertThat(p0Secondary).isNotNull();
        assertThat(p0Secondary.getPropertyName()).isEqualTo("secondaryProgress");
        assertThat(p0Secondary.toString()).isEqualTo(getPropertyValuesToString("secondaryProgress", "400", "600"));

        assertThat(p0Max).isNotNull();
        assertThat(p0Max.getPropertyName()).isEqualTo("max");
        assertThat(p0Max.toString()).isEqualTo(getPropertyValuesToString("max", "1000", "1000"));
    }

    @Test public void testShouldAdvanceWithCorrectValuesWhen2LevelsUpOrMore()
    {
        userLevelProgressBar.startsWith(200);
        assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("1");

        List<Animator> animators = userLevelProgressBar.getAnimatorQueue(2700);
        assertThat(animators).isNotNull();
        assertThat(animators.size()).isEqualTo(3);

        //Level 1 - 2 animation
        ObjectAnimator objectAnimator0 = (ObjectAnimator) animators.get(0);

        PropertyValuesHolder[] propertyValuesHolders0 = objectAnimator0.getValues();

        assertThat(propertyValuesHolders0).isNotNull();
        assertThat(propertyValuesHolders0.length).isEqualTo(3);

        PropertyValuesHolder p0Progress = propertyValuesHolders0[0];
        PropertyValuesHolder p0Secondary = propertyValuesHolders0[1];
        PropertyValuesHolder p0Max = propertyValuesHolders0[2];

        assertThat(p0Progress).isNotNull();
        assertThat(p0Progress.getPropertyName()).isEqualTo("progress");
        assertThat(p0Progress.toString()).isEqualTo(getPropertyValuesToString("progress", "200", "200"));

        assertThat(p0Secondary).isNotNull();
        assertThat(p0Secondary.getPropertyName()).isEqualTo("secondaryProgress");
        assertThat(p0Secondary.toString()).isEqualTo(getPropertyValuesToString("secondaryProgress", "200", "1000"));

        assertThat(p0Max).isNotNull();
        assertThat(p0Max.getPropertyName()).isEqualTo("max");
        assertThat(p0Max.toString()).isEqualTo(getPropertyValuesToString("max", "1000", "1000"));

        //Level 2 - 3 animation
        ObjectAnimator objectAnimator1 = (ObjectAnimator) animators.get(1);

        PropertyValuesHolder[] propertyValuesHolders1 = objectAnimator1.getValues();

        assertThat(propertyValuesHolders1).isNotNull();
        assertThat(propertyValuesHolders1.length).isEqualTo(3);

        PropertyValuesHolder p1Progress = propertyValuesHolders1[0];
        PropertyValuesHolder p1Secondary = propertyValuesHolders1[1];
        PropertyValuesHolder p1Max = propertyValuesHolders1[2];

        assertThat(p1Progress).isNotNull();
        assertThat(p1Progress.getPropertyName()).isEqualTo("progress");
        assertThat(p1Progress.toString()).isEqualTo(getPropertyValuesToString("progress", "0", "0"));

        assertThat(p1Secondary).isNotNull();
        assertThat(p1Secondary.getPropertyName()).isEqualTo("secondaryProgress");
        assertThat(p1Secondary.toString()).isEqualTo(getPropertyValuesToString("secondaryProgress", "0", "999"));

        assertThat(p1Max).isNotNull();
        assertThat(p1Max.getPropertyName()).isEqualTo("max");
        assertThat(p1Max.toString()).isEqualTo(getPropertyValuesToString("max", "999", "999"));

        //Level 3 - expectedXp animation
        ObjectAnimator objectAnimator2 = (ObjectAnimator) animators.get(2);

        PropertyValuesHolder[] propertyValuesHolders2 = objectAnimator2.getValues();

        assertThat(propertyValuesHolders2).isNotNull();
        assertThat(propertyValuesHolders2.length).isEqualTo(3);

        PropertyValuesHolder p2Progress = propertyValuesHolders2[0];
        PropertyValuesHolder p2Secondary = propertyValuesHolders2[1];
        PropertyValuesHolder p2Max = propertyValuesHolders2[2];

        assertThat(p2Progress).isNotNull();
        assertThat(p2Progress.getPropertyName()).isEqualTo("progress");
        assertThat(p2Progress.toString()).isEqualTo(getPropertyValuesToString("progress", "0", "0"));

        assertThat(p2Secondary).isNotNull();
        assertThat(p2Secondary.getPropertyName()).isEqualTo("secondaryProgress");
        assertThat(p2Secondary.toString()).isEqualTo(getPropertyValuesToString("secondaryProgress", "0", "899"));

        assertThat(p2Max).isNotNull();
        assertThat(p2Max.getPropertyName()).isEqualTo("max");
        assertThat(p2Max.toString()).isEqualTo(getPropertyValuesToString("max", "999", "999"));
    }

    @Test public void testShouldAdvanceWithCorrectValuesWhenLevelis2()
    {
        userLevelProgressBar.startsWith(1200);
        assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("2");

        List<Animator> animators = userLevelProgressBar.getAnimatorQueue(700);
        assertThat(animators).isNotNull();

        ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(0);

        PropertyValuesHolder[] propertyValuesHolders = objectAnimator.getValues();

        assertThat(propertyValuesHolders).isNotNull();
        assertThat(propertyValuesHolders.length).isEqualTo(3);

        PropertyValuesHolder p0Progress = propertyValuesHolders[0];
        PropertyValuesHolder p0Secondary = propertyValuesHolders[1];
        PropertyValuesHolder p0Max = propertyValuesHolders[2];

        assertThat(p0Progress).isNotNull();
        assertThat(p0Progress.getPropertyName()).isEqualTo("progress");
        assertThat(p0Progress.toString()).isEqualTo(getPropertyValuesToString("progress", "199", "199"));

        assertThat(p0Secondary).isNotNull();
        assertThat(p0Secondary.getPropertyName()).isEqualTo("secondaryProgress");
        assertThat(p0Secondary.toString()).isEqualTo(getPropertyValuesToString("secondaryProgress", "199", "899"));

        assertThat(p0Max).isNotNull();
        assertThat(p0Max.getPropertyName()).isEqualTo("max");
        assertThat(p0Max.toString()).isEqualTo(getPropertyValuesToString("max", "999", "999"));
    }

    @Test public void testShouldAdvanceWithCorrectValuesWhenAdvancedToMaxLevel()
    {
        userLevelProgressBar.startsWith(2700);
        assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("3");

        List<Animator> animators = userLevelProgressBar.getAnimatorQueue(301);
        assertThat(animators).isNotNull();

        ObjectAnimator objectAnimator = (ObjectAnimator) animators.get(0);

        PropertyValuesHolder[] propertyValuesHolders = objectAnimator.getValues();

        assertThat(propertyValuesHolders).isNotNull();
        assertThat(propertyValuesHolders.length).isEqualTo(3);

        PropertyValuesHolder p0Progress = propertyValuesHolders[0];
        PropertyValuesHolder p0Secondary = propertyValuesHolders[1];
        PropertyValuesHolder p0Max = propertyValuesHolders[2];

        assertThat(p0Progress).isNotNull();
        assertThat(p0Progress.getPropertyName()).isEqualTo("progress");
        assertThat(p0Progress.toString()).isEqualTo(getPropertyValuesToString("progress", "699", "699"));

        assertThat(p0Secondary).isNotNull();
        assertThat(p0Secondary.getPropertyName()).isEqualTo("secondaryProgress");
        assertThat(p0Secondary.toString()).isEqualTo(getPropertyValuesToString("secondaryProgress", "699", "999"));

        assertThat(p0Max).isNotNull();
        assertThat(p0Max.getPropertyName()).isEqualTo("max");
        assertThat(p0Max.toString()).isEqualTo(getPropertyValuesToString("max", "999", "999"));

        //TODO find a way for robolectric to run animation
        //AnimatorSet animatorSet = new AnimatorSet();
        //animatorSet.playSequentially(animators);
        //animatorSet.start();
        //
        //Robolectric.runUiThreadTasksIncludingDelayedTasks();
        //Robolectric.runUiThreadTasks();
        //
        //assertThat(userLevelProgressBar.getCurrentLevel()).isEqualTo("4");
        //assertThat nextLevelLabel is Hidden
        //assertThat

    }

    private String getPropertyValuesToString(String propertyName, String startValue, String endValue)
    {
        return String.format("%s:  %s  %s  ", propertyName, startValue, endValue);
    }
}
