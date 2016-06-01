package com.ayondo.academy.models.level;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.level.LevelDefDTO;
import com.ayondo.academy.api.level.LevelDefDTOList;
import com.ayondo.academy.persistence.level.LevelDefListCacheRx;
import java.util.Random;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LevelDefDTOListTest
{
    @Inject LevelDefListCacheRx levelDefListCache;

    @Test
    public void testShouldReturnNull()
    {
        int xp = new Random().nextInt(Integer.MAX_VALUE);
        LevelDefDTOList levelDefDTOList = new LevelDefDTOList();
        LevelDefDTO levelDefDTO = levelDefDTOList.findCurrentLevel(xp);
        assertThat(levelDefDTO).isNull();
        LevelDefDTO nextLevelDefDTO = levelDefDTOList.getNextLevelDTO(1);
        assertThat(nextLevelDefDTO).isNull();
    }

    @Test
    public void testShouldReturnCorrectLevel()
    {
        int xp = 2000;

        LevelDefDTOList levelDefDTOList = createMockList();

        LevelDefDTO current = levelDefDTOList.findCurrentLevel(xp);
        assertThat(current).isNotNull();
        assertThat(current.level).isEqualTo(2);
    }

    @Test
    public void testShouldReturnNextLevel()
    {
        int level = 2;

        LevelDefDTOList levelDefDTOList = createMockList();

        LevelDefDTO next = levelDefDTOList.getNextLevelDTO(level);
        assertThat(next).isNotNull();
        assertThat(next.level).isEqualTo(3);
    }

    @Test
    public void testShouldReturnMaxLevel()
    {
        LevelDefDTOList levelDefDTOList = createMockList();

        int level = levelDefDTOList.size();

        LevelDefDTO max = levelDefDTOList.getNextLevelDTO(level);
        assertThat(max).isNotNull();
        assertThat(max.level).isEqualTo(level);
        assertThat(max).isEqualTo(levelDefDTOList.getMaxLevelDTO());
    }

    private LevelDefDTOList createMockList()
    {
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

        LevelDefDTOList levelDefDTOList = new LevelDefDTOList();
        levelDefDTOList.add(mockLevel1);
        levelDefDTOList.add(mockLevel3);
        levelDefDTOList.add(mockLevel2);

        //Override add method and sort whenever there's an add?
        levelDefDTOList.sort();

        return levelDefDTOList;
    }
}
