import com.google.samples.apps.nowinandroid.core.domain.GetFollowableTopicsUseCase
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testUserData = UserData(
        bookmarkedNewsResources = emptySet(),
        viewedNewsResources = emptySet(),
        followedTopics = setOf("topic1", "topic3"),
        themeBrand = ThemeBrand.DEFAULT,
        darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
        useDynamicColor = false,
        shouldHideOnboarding = true
    )

    private val testTopics = listOf(
        Topic("topic1", "Zebra", "desc1", "longDesc1", "url1", "imageUrl1"),
        Topic("topic2", "Apple", "desc2", "longDesc2", "url2", "imageUrl2"),
        Topic("topic3", "Banana", "desc3", "longDesc3", "url3", "imageUrl3")
    )

    @Test
    fun `topics remain unsorted when no sortBy parameter is provided`() = runTest {
        val topicsRepository = object : TopicsRepository {
            override fun getTopics() = kotlinx.coroutines.flow.flow { emit(testTopics) }
            override suspend fun sync() {}
        }

        val userDataRepository = object : UserDataRepository {
            override val userData = kotlinx.coroutines.flow.flow { emit(testUserData) }
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
        val result = useCase().first()

        assertEquals(testTopics.size, result.size)
        assertEquals("Zebra", result[0].topic.name)
        assertEquals("Apple", result[1].topic.name)
        assertEquals("Banana", result[2].topic.name)
    }

    @Test
    fun `topics are alphabetically sorted when sortBy is NAME`() = runTest {
        val topicsRepository = object : TopicsRepository {
            override fun getTopics() = kotlinx.coroutines.flow.flow { emit(testTopics) }
            override suspend fun sync() {}
        }

        val userDataRepository = object : UserDataRepository {
            override val userData = kotlinx.coroutines.flow.flow { emit(testUserData) }
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
        val result = useCase(TopicSortField.NAME).first()

        assertEquals(testTopics.size, result.size)
        assertEquals("Apple", result[0].topic.name)
        assertEquals("Banana", result[1].topic.name)
        assertEquals("Zebra", result[2].topic.name)
    }
}