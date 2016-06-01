package com.ayondo.academy.persistence;

import com.ayondo.academy.persistence.achievement.PersistenceAchievementTestModule;
import com.ayondo.academy.persistence.security.PersistenceSecurityTestModule;
import com.ayondo.academy.persistence.translation.PersistenceTranslationTestModule;
import dagger.Module;

@Module(
        includes = {
                PersistenceSecurityTestModule.class,
                PersistenceTranslationTestModule.class,
                PersistenceAchievementTestModule.class,
        },
        library = true,
        complete = false,
        overrides = true
)
public class PersistenceTestModule
{
}
