package com.tradehero.th.api.portfolio;

import com.tradehero.th.api.users.UserBaseDTO;
import java.sql.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/** Created with IntelliJ IDEA. User: xavier Date: 10/24/13 Time: 7:53 PM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class PortfolioDTOComparableTest
{
    public static final String TAG = PortfolioDTOComparableTest.class.getSimpleName();

    @Before public void setUp()
    {
        PortfolioDTO.currentUserBase = getCurrentUser();
    }

    @After public void tearDown()
    {
        PortfolioDTO.currentUserBase = null;
    }

    private UserBaseDTO getCurrentUser()
    {
        UserBaseDTO currentUser = new UserBaseDTO();
        currentUser.firstName = "iamcurrent";
        currentUser.id = 10;
        return currentUser;
    }

    private UserBaseDTO getOtherUser1()
    {
        UserBaseDTO otherUser = new UserBaseDTO();
        otherUser.firstName = "user1";
        otherUser.id = 5;
        return otherUser;
    }

    private UserBaseDTO getOtherUser2()
    {
        UserBaseDTO otherUser = new UserBaseDTO();
        otherUser.firstName = "user2";
        otherUser.id = 15;
        return otherUser;
    }

    private PortfolioDTO getCurrentUserDefaultPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = PortfolioCompactDTO.DEFAULT_TITLE;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserOlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = "somewhatolder";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserYoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getCurrentUser());
        portfolioDTO.title = "somewhatyounger";
        portfolioDTO.creationDate = new Date(2013, 1, 2);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser1());
        portfolioDTO.title = "nullDate";
        return portfolioDTO;
    }

    private PortfolioDTO getUser1OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser1());
        portfolioDTO.title = "somewhatolder";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser1());
        portfolioDTO.title = "somewhatyounger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser2());
        portfolioDTO.title = "nullDate";
        return portfolioDTO;
    }

    private PortfolioDTO getUser2OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser2());
        portfolioDTO.title = "somewhatolder";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setOwnerDTO(getOtherUser2());
        portfolioDTO.title = "somewhatyounger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    @Test public void currentUserShouldBeEqualToItself()
    {
        assertTrue(getCurrentUser().equals(getCurrentUser()));
    }

    @Test public void currentUserShouldBeDifferentFromOthers()
    {
        assertFalse(getCurrentUser().equals(getOtherUser1()));
        assertFalse(getCurrentUser().equals(getOtherUser2()));
    }

    @Test public void shouldOrderAsExpected()
    {
        Set<PortfolioDTO> treeSet = new TreeSet<>();

        treeSet.add(getCurrentUserOlderPortfolio());
        treeSet.add(getUser1YoungerPortfolio());
        treeSet.add(getUser2YoungerPortfolio());
        treeSet.add(getUser1NullDatePortfolio());
        treeSet.add(getUser2NullDatePortfolio());
        treeSet.add(getCurrentUserYoungerPortfolio());
        treeSet.add(getCurrentUserDefaultPortfolio());
        treeSet.add(getUser1OlderPortfolio());
        treeSet.add(getUser2OlderPortfolio());

        Iterator<PortfolioDTO> iterator = treeSet.iterator();
        assertThat(iterator.next().compareTo(getCurrentUserDefaultPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserOlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserYoungerPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1NullDatePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1OlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser1YoungerPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2NullDatePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2OlderPortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getUser2YoungerPortfolio()), equalTo(0));
    }
}
