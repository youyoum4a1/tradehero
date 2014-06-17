package com.tradehero.th.api.portfolio;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.users.UserBaseDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
@Config(manifest = Config.NONE)
public class DisplayablePortfolioDTOComparableTest
{
    @Before public void setUp() throws IOException
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        THJsonAdapter.getInstance().toBody(getCurrentUser()).writeTo(byteArrayOutputStream);
    }

    @After public void tearDown()
    {
        // Clear THUser?
    }

    /**
     * User ids:
     * user1: 5
     * current: 10
     * user2: 15
     */
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

    /**
     * Portfolio ids:
     * current user default: 5
     * current user null date: 10
     * current user older: 15
     * current user younger: 20
     * user1 null date: 25
     * user1 default: 27
     * user1 older: 30
     * user1 younger: 35
     * user2 null date: 40
     * user2 older: 45
     * user2 younger: 50
     * null user: 55
     * @return
     */
    private OwnedPortfolioId getCurrentUserDefaultPortfolioId()
    {
        return new OwnedPortfolioId(10, 5);
    }

    private OwnedPortfolioId getCurrentUserNullDatePortfolioId()
    {
        return new OwnedPortfolioId(10, 10);
    }

    private OwnedPortfolioId getCurrentUserOlderPortfolioId()
    {
        return new OwnedPortfolioId(10, 15);
    }

    private OwnedPortfolioId getCurrentUserYoungerPortfolioId()
    {
        return new OwnedPortfolioId(10, 20);
    }

    private OwnedPortfolioId getUser1NullDatePortfolioId()
    {
        return new OwnedPortfolioId(5, 25);
    }

    private OwnedPortfolioId getUser1DefaultPortfolioId()
    {
        return new OwnedPortfolioId(5, 27);
    }

    private OwnedPortfolioId getUser1OlderPortfolioId()
    {
        return new OwnedPortfolioId(5, 30);
    }

    private OwnedPortfolioId getUser1YoungerPortfolioId()
    {
        return new OwnedPortfolioId(5, 35);
    }

    private OwnedPortfolioId getUser2NullDatePortfolioId()
    {
        return new OwnedPortfolioId(15, 40);
    }

    private OwnedPortfolioId getUser2OlderPortfolioId()
    {
        return new OwnedPortfolioId(15, 45);
    }

    private OwnedPortfolioId getUser2YoungerPortfolioId()
    {
        return new OwnedPortfolioId(15, 50);
    }

    private PortfolioDTO getNullUserPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 55;
        portfolioDTO.title = "userNull";
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserDefaultPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 5;
        portfolioDTO.title = PortfolioCompactDTO.DEFAULT_TITLE;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserNullCreationPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 10;
        portfolioDTO.title = "currentUser NullCreation";
        portfolioDTO.creationDate = null;
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserOlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 15;
        portfolioDTO.title = "CurrentUserId Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getCurrentUserYoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 20;
        portfolioDTO.title = "CurrentUserId Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 2);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 25;
        portfolioDTO.title = "User1 NullCreation";
        return portfolioDTO;
    }

    private PortfolioDTO getUser1DefaultPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 27;
        portfolioDTO.title = "Default";
        portfolioDTO.creationDate = new Date(2013, 2, 1); // Should be irrelevant
        return portfolioDTO;
    }

    private PortfolioDTO getUser1OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 30;
        portfolioDTO.title = "User1 Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser1YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 35;
        portfolioDTO.title = "User1 Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2NullDatePortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 40;
        portfolioDTO.title = "User2 NullCreation";
        return portfolioDTO;
    }

    private PortfolioDTO getUser2OlderPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 45;
        portfolioDTO.title = "User2 Older";
        portfolioDTO.creationDate = new Date(2013, 1, 1);
        return portfolioDTO;
    }

    private PortfolioDTO getUser2YoungerPortfolio()
    {
        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.id = 50;
        portfolioDTO.title = "User2 Younger";
        portfolioDTO.creationDate = new Date(2013, 1, 5);
        return portfolioDTO;
    }

    private DisplayablePortfolioDTO getNullUserDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(null, null, getNullUserPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserDefaultDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserDefaultPortfolioId(), getCurrentUser(), getCurrentUserDefaultPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserNullCreationDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserNullDatePortfolioId(), getCurrentUser(), getCurrentUserNullCreationPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserOlderDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserOlderPortfolioId(), getCurrentUser(), getCurrentUserOlderPortfolio());
    }

    private DisplayablePortfolioDTO getCurrentUserYoungerDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getCurrentUserYoungerPortfolioId(), getCurrentUser(), getCurrentUserYoungerPortfolio());
    }

    private DisplayablePortfolioDTO getUser1NullDateDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1NullDatePortfolioId(), getOtherUser1(), getUser1NullDatePortfolio());
    }

    private DisplayablePortfolioDTO getUser1DefaultDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1DefaultPortfolioId(), getOtherUser1(), getUser1DefaultPortfolio());
    }

    private DisplayablePortfolioDTO getUser1OlderDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1OlderPortfolioId(), getOtherUser1(), getUser1OlderPortfolio());
    }

    private DisplayablePortfolioDTO getUser1YoungerDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser1YoungerPortfolioId(), getOtherUser1(), getUser1YoungerPortfolio());
    }

    private DisplayablePortfolioDTO getUser2NullDateDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser2NullDatePortfolioId(), getOtherUser2(), getUser2NullDatePortfolio());
    }

    private DisplayablePortfolioDTO getUser2OlderDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser2OlderPortfolioId(), getOtherUser2(), getUser2OlderPortfolio());
    }

    private DisplayablePortfolioDTO getUser2YoungerDisplayablePortfolio()
    {
        return new DisplayablePortfolioDTO(getUser2YoungerPortfolioId(), getOtherUser2(), getUser2YoungerPortfolio());
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

    @Test public void shouldOrderOldYoung()
    {
        Set<DisplayablePortfolioDTO> treeSet = new TreeSet<>();
        treeSet.add(getCurrentUserOlderDisplayablePortfolio());
        treeSet.add(getCurrentUserYoungerDisplayablePortfolio());

        assertThat(treeSet.size(), equalTo(2));

        Iterator<DisplayablePortfolioDTO> iterator = treeSet.iterator();
        assertThat(iterator.next().compareTo(getCurrentUserOlderDisplayablePortfolio()), equalTo(0));
        assertThat(iterator.next().compareTo(getCurrentUserYoungerDisplayablePortfolio()), equalTo(0));
    }

    @Test public void shouldOrderAsExpected()
    {
        Set<DisplayablePortfolioDTO> treeSet = new TreeSet<>();

        treeSet.add(getUser1YoungerDisplayablePortfolio());
        treeSet.add(getUser2YoungerDisplayablePortfolio());
        treeSet.add(getUser1NullDateDisplayablePortfolio());
        treeSet.add(getNullUserDisplayablePortfolio());
        treeSet.add(getCurrentUserNullCreationDisplayablePortfolio());
        treeSet.add(getUser2NullDateDisplayablePortfolio());
        treeSet.add(getCurrentUserYoungerDisplayablePortfolio());
        treeSet.add(getCurrentUserDefaultDisplayablePortfolio());
        treeSet.add(getUser1OlderDisplayablePortfolio());
        treeSet.add(getUser1DefaultDisplayablePortfolio());
        treeSet.add(getUser2OlderDisplayablePortfolio());
        treeSet.add(getCurrentUserOlderDisplayablePortfolio());

        assertThat(treeSet.size(), equalTo(12));

        List<DisplayablePortfolioDTO> expectedList = new ArrayList<>();
        expectedList.add(getNullUserDisplayablePortfolio());
        expectedList.add(getUser1NullDateDisplayablePortfolio());
        expectedList.add(getUser1DefaultDisplayablePortfolio());
        expectedList.add(getUser1OlderDisplayablePortfolio());
        expectedList.add(getUser1YoungerDisplayablePortfolio());
        expectedList.add(getCurrentUserDefaultDisplayablePortfolio());
        expectedList.add(getCurrentUserNullCreationDisplayablePortfolio());
        expectedList.add(getCurrentUserOlderDisplayablePortfolio());
        expectedList.add(getCurrentUserYoungerDisplayablePortfolio());
        expectedList.add(getUser2NullDateDisplayablePortfolio());
        expectedList.add(getUser2OlderDisplayablePortfolio());
        expectedList.add(getUser2YoungerDisplayablePortfolio());

        assertEquals(expectedList, new ArrayList<>(treeSet));
    }
}
