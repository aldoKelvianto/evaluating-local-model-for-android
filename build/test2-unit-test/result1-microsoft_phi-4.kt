```kotlin
package com.google.samples.apps.nowinandroid.core.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val topicsRepository = object : TopicsRepository {
        override suspend fun getTopics(): kotlinx.coroutines.flow.Flow<List<Topic>> =
            kotlinx.coroutines.flow.flowOf(
                listOf(
                    Topic("1", "Zebra", "", "", "", ""),
                    Topic("2", "Apple", "", "", "", "")
                )
            )

        override suspend fun getTopic(id: String): kotlinx.coroutines.flow.Flow<Topic> {
            throw NotImplementedError()
        }
    }

    private val userDataRepository = object : UserDataRepository {
        override val userData: kotlinx.coroutines.flow.Flow<UserData> =
            kotlinx.coroutines.flow.flowOf(
                UserData(setOf("1"), setOf(), setOf("1", "2"), ThemeBrand.DEFAULT, DarkThemeConfig.DEFAULT, false, false)
            )

        override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {}
        override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {}
        override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {}
        override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {}
        override suspend fun setThemeBrand(themeBrand: ThemeBrand) {}
        override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {}
        override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {}
        override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {}
    }

    private val useCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)

    @Test
    fun `default sorting should return topics unsorted`() = runTest {
        val result = useCase().first()
        assertEquals(listOf(
            FollowableTopic(Topic("1", "Zebra", "", "", "", ""), true),
            FollowableTopic(Topic("2", "Apple", "", "", "", ""), true)
        ), result)
    }

    @Test
    fun `name sorting should return topics alphabetically sorted`() = runTest {
        val result = useCase(NAME).first()
        assertEquals(listOf(
            FollowableTopic(Topic("2", "Apple", "", "", "", ""), true),
            FollowableTopic(Topic("1", "Zebra", "", "", "", ""), true)
        ), result)
    }
}
```