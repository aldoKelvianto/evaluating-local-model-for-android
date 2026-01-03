package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class GetFollowableTopicsUseCaseTest {

    private val topics = listOf(
        Topic("1", "Alpha", "", "", "", ""),
        Topic("2", "Beta", "", "", "", ""),
        Topic("3", "Gamma", "", "", "", "")
    )

    private val userDataFlow = MutableStateFlow(
        UserData(
            bookmarkedNewsResources = emptySet(),
            viewedNewsResources = emptySet(),
            followedTopics = setOf("2"),
            themeBrand = com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand.DEFAULT,
            darkThemeConfig = com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig.SYSTEM_DEFAULT,
            useDynamicColor = false,
            shouldHideOnboarding = false
        )
    )

    private val topicsRepository = object : TopicsRepository {
        override fun getTopics(): Flow<List<Topic>> = MutableStateFlow(topics)
        override suspend fun sync() {}
    }

    private val userDataRepository = object : UserDataRepository {
        override val userData: Flow<UserData> = userDataFlow
        override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {}
        override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {}
        override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {}
        override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {}
        override suspend fun setThemeBrand(themeBrand: com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand) {}
        override suspend fun setDarkThemeConfig(darkThemeConfig: com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig) {}
        override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {}
        override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {}
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)

    @Test
    fun `default sorting returns topics in original order`() = runTest {
        val result = useCase().first()
        assertEquals(
            listOf(
                FollowableTopic(topics[0], false),
                FollowableTopic(topics[1], true),
                FollowableTopic(topics[2], false)
            ),
            result
        )
    }

    @Test
    fun `name sorting returns topics alphabetically sorted`() = runTest {
        val result = useCase(TopicSortField.NAME).first()
        assertEquals(
            listOf(
                FollowableTopic(topics[0], false),
                FollowableTopic(topics[1], true),
                FollowableTopic(topics[2], false)
            ),
            result
        )
    }
}