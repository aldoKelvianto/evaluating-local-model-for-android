```kotlin
package com.google.samples.apps.nowinandroid.core.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetFollowableTopicsUseCaseTest {

    private lateinit var topicsRepository: FakeTopicsRepository
    private lateinit var userDataRepository: FakeUserDataRepository
    private lateinit var getFollowableTopicsUseCase: GetFollowableTopicsUseCase

    @Before
    fun setup() {
        topicsRepository = FakeTopicsRepository()
        userDataRepository = FakeUserDataRepository()
        getFollowableTopicsUseCase = GetFollowableTopicsUseCase(topicsRepository, userDataRepository)
    }

    @Test
    fun `default sorting - verify topics remain unsorted when no sortBy parameter is provided` = runBlocking {
        val topic1 = Topic("id1", "Topic B", "desc1", "longDesc1", "url1", "imageUrl1")
        val topic2 = Topic("id2", "Topic A", "desc2", "longDesc2", "url2", "imageUrl2")
        topicsRepository.setTopics(listOf(topic1, topic2))

        val followedTopics = getFollowableTopicsUseCase().first()

        assertEquals(listOf(topic1, topic2), followedTopics)
    }

    @Test
    fun `name sorting - verify topics are alphabetically sorted when sortBy = NAME is provided` = runBlocking {
        val topic1 = Topic("id1", "Topic B", "desc1", "longDesc1", "url1", "imageUrl1")
        val topic2 = Topic("id2", "Topic A", "desc2", "longDesc2", "url2", "imageUrl2")
        topicsRepository.setTopics(listOf(topic1, topic2))

        val followedTopics = getFollowableTopicsUseCase(sortBy = TopicSortField.NAME).first()

        assertEquals(listOf(topic2, topic1), followedTopics)
    }
}

class FakeTopicsRepository : TopicsRepository {
    private var topics: List<Topic> = emptyList()

    override fun getTopics(): kotlinx.coroutines.flow.Flow<List<Topic>> {
        return kotlinx.coroutines.flow.flowOf(topics)
    }

    override fun getTopic(id: String): kotlinx.coroutines.flow.Flow<Topic> {
        TODO("Not implemented")
    }

    override fun sync() {
        TODO("Not implemented")
    }

    fun setTopics(list: List<Topic>) {
        topics = list
    }
}

class FakeUserDataRepository : UserDataRepository {
    private var userData: UserData = UserData(emptySet(), emptySet(), emptySet(), ThemeBrand.DEFAULT, DarkThemeConfig.SYSTEM, false, false)

    override val userData: kotlinx.coroutines.flow.Flow<UserData> {
        return kotlinx.coroutines.flow.flowOf(userData)
    }

    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) {
        userData = userData.copy(followedTopics = followedTopicIds)
    }

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        TODO("Not implemented")
    }

    override suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        TODO("Not implemented")
    }

    override suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        TODO("Not implemented")
    }

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        TODO("Not implemented")
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        TODO("Not implemented")
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        TODO("Not implemented")
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        TODO("Not implemented")
    }
}
```