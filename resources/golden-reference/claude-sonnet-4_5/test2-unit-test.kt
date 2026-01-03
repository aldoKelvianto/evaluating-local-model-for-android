package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testTopics = listOf(
        Topic(
            id = "1",
            name = "Kotlin",
            shortDescription = "",
            longDescription = "",
            url = "",
            imageUrl = ""
        ),
        Topic(
            id = "2",
            name = "Android",
            shortDescription = "",
            longDescription = "",
            url = "",
            imageUrl = ""
        ),
        Topic(
            id = "3",
            name = "Testing",
            shortDescription = "",
            longDescription = "",
            url = "",
            imageUrl = ""
        )
    )

    private val testUserData = UserData(
        bookmarkedNewsResources = emptySet(),
        viewedNewsResources = emptySet(),
        followedTopics = setOf("1"),
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        useDynamicColor = false,
        shouldHideOnboarding = false
    )

    private val topicsRepository = object : TopicsRepository {
        override fun getTopics() = flowOf(testTopics)
        override fun getTopic(id: String) = flowOf(testTopics.first { it.id == id })
        override suspend fun syncWith(synchronizer: com.google.samples.apps.nowinandroid.core.data.Synchronizer) = true
    }

    private val userDataRepository = object : UserDataRepository {
        override val userData = flowOf(testUserData)
        override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {}
        override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {}
        override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {}
        override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {}
        override suspend fun setThemeBrand(themeBrand: ThemeBrand) {}
        override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {}
        override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {}
        override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {}
    }

    @Test
    fun topicsAreNotSortedByDefaultWhenNoSortByParameterProvided() = runTest {
        val useCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)
        val result = useCase().first()
        
        assertEquals("Kotlin", result[0].topic.name)
        assertEquals("Android", result[1].topic.name)
        assertEquals("Testing", result[2].topic.name)
    }

    @Test
    fun topicsAreSortedAlphabeticallyWhenSortByNameProvided() = runTest {
        val useCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)
        val result = useCase(TopicSortField.NAME).first()
        
        assertEquals("Android", result[0].topic.name)
        assertEquals("Kotlin", result[1].topic.name)
        assertEquals("Testing", result[2].topic.name)
    }
}