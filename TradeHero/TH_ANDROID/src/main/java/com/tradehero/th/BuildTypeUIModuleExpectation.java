package com.tradehero.th;

public interface BuildTypeUIModuleExpectation
{
    // Annotations are here only as hints
    /*@ForAchievementListTestingFragment*/ Class provideAchievementListTestingFragmentClass();
    /*@ForQuestListTestingFragment*/ Class provideQuestListTestingFragmentClass();
    /*@ForXpTestingFragment*/ Class provideXpTestingFragmentClass();
    /*@ForTypographyFragment*/ Class provideTypographyExampleFragment();
}
