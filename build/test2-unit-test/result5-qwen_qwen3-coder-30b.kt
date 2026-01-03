```kotlin
package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `invoke with default sortBy parameter returns topics in original order`() = runTest {
        // Given
        val topic1 = Topic("1", "B Topic", "", "", "", "")
        val topic2 = Topic("2", "A Topic", "", "", "", "")
        val topics = listOf(topic1, topic2)
        val userData = UserData(
            bookmarkedNewsResources = emptySet(),
            viewedNewsResources = emptySet(),
            followedTopics = emptySet(),
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = false
        )

        val topicsRepository = object : TopicsRepository {
            override fun getTopics() = flowOf(topics)
            override suspend fun sync() {}
        }

        val userDataRepository = object : UserDataRepository {
            override val userData = flowOf(userData)
            override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {}
            override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {}
            override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {}
            override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {}
            override suspend fun setThemeBrand(themeBrand: ThemeBrand) {}
            override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {}
            override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {}
            override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {}
        }

        val useCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)

        // When
        val result = useCase().first()

        // Then
        assertEquals(topic1.name, result[0].topic.name)
        assertEquals(topic2.name, result[1].topic.name)
    }

    @Test
    fun `invoke with NAME sortBy parameter returns topics sorted alphabetically`() = runTest {
        // Given
        val topic1 = Topic("1", "B Topic", "", "", "", "")
        val topic2 = Topic("2", "A Topic", "", "", "", "")
        val topics = listOf(topic1, topic2)
        val userData = UserData(
            bookmarkedNewsResources = emptySet(),
            viewedNewsResources = emptySet(),
            followedTopics = emptySet(),
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = false
        )

        val topicsRepository = object : TopicsRepository {
            override fun getTopics() = flowOf(topics)
            override suspend fun sync() {}
        }

        val userDataRepository = object : UserDataRepository {
            override val userData = flowOf(userData)
            override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {}
            override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {}
            override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {}
            override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {}
            override suspend fun setThemeBrand(themeBrand: ThemeBrand) {}
            override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {}
            override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {}
            override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {}
        }

        val useCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)

        // When
        val result = useCase(sortBy = TopicSortField.NAME).first()

        // Then
        assertEquals(topic2.name, result[0].topic.name)
        assertEquals(topic1.name, result[1].topic.name)
    }
}
```