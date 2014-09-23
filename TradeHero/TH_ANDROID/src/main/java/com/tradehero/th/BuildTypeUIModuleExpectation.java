package com.tradehero.th;

public interface BuildTypeUIModuleExpectation
{
    // Annotations are here only as hints
    /*@ForAchievementListFragment*/ Class provideAchievementListFragmentClass();
    /*@ForAchievementListTestingFragment*/ Class provideAchievementListTestingFragmentClass();
    /*@ForQuestListTestingFragment*/ Class provideQuestListTestingFragmentClass();
    /*@ForXpTestingFragment*/ Class provideXpTestingFragmentClass();
}
