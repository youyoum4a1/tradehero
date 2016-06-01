package com.ayondo.academy.api.position;

import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class OwnedPositionIdTest
{
    @Test public void testOwnedPositionComparison()
    {
        OwnedPositionId ownedPositionId1 = new OwnedPositionId(108805, 922073, 1736788);
        OwnedPortfolioId ownedPositionId2 = new OwnedPositionId(108805, 922073, 1966975);

        OwnedPortfolioId ownedPortfolioId1 = new OwnedPortfolioId(108805, 922073);

        assertThat(ownedPositionId1.equals(ownedPositionId2)).isFalse();
        assertThat(ownedPositionId1.equals(ownedPortfolioId1)).isTrue();
    }
}