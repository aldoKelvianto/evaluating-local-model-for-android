Context: Android developer using Kotlin and other relevant testing libraries for testing Android code

Task: Generate unit tests for the provided UseCase below

Requirements:
Create exactly 2 unit test methods covering these scenarios:
1. Default sorting - Verify topics remain unsorted when no sortBy parameter is provided
2. Name sorting - Verify topics are alphabetically sorted when sortBy = NAME is provided

Constraints:
- Don't use mocking library (or introduce any new libraries)
- Use meaningful test method names
- Include proper assertions
- Include all necessary imports and dependencies
- Use the attached source code as reference
- The output code is a new file under the test folder

Expected output:
- Only valid Kotlin code in plain text format — no comments, explanations, markdown, backticks, or any other non-code content.
- Class name is GetFollowableTopicsUseCaseTest

Source Code:
```kotlin
package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField.NAME
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField.NONE
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which obtains a list of topics with their followed state.
 */
class GetFollowableTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
    private val userDataRepository: UserDataRepository,
) {
    /**
     * Returns a list of topics with their associated followed state.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke(sortBy: TopicSortField = NONE): Flow<List<FollowableTopic>> = combine(
        userDataRepository.userData,
        topicsRepository.getTopics(),
    ) { userData, topics ->
        val followedTopics = topics
            .map { topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics,
                )
            }
        when (sortBy) {
            NAME -> followedTopics.sortedBy { it.topic.name }
            else -> followedTopics
        }
    }
}

enum class TopicSortField {
    NONE,
    NAME,
}


package com.google.samples.apps.nowinandroid.core.testing.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit [TestRule] that sets the Main dispatcher to [testDispatcher]
 * for the duration of the test.
 */
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(testDispatcher)

    override fun finished(description: Description) = Dispatchers.resetMain()
}

package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.data.Syncable
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import kotlinx.coroutines.flow.Flow

interface TopicsRepository : Syncable {
    /**
     * Gets the available topics as a stream
     */
    fun getTopics(): Flow<List<Topic>>

    /**
     * Gets data for a specific topic
     */
    fun getTopic(id: String): Flow<Topic>
}


package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.model.data.DarkThemeConfig
import com.google.samples.apps.nowinandroid.core.model.data.ThemeBrand
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    /**
     * Stream of [UserData]
     */
    val userData: Flow<UserData>

    /**
     * Sets the user's currently followed topics
     */
    suspend fun setFollowedTopicIds(followedTopicIds: Set<String>)

    /**
     * Sets the user's newly followed/unfollowed topic
     */
    suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean)

    /**
     * Updates the bookmarked status for a news resource
     */
    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean)

    /**
     * Updates the viewed status for a news resource
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean)

    /**
     * Sets the desired theme brand.
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand)

    /**
     * Sets the desired dark theme config.
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the preferred dynamic color config.
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean)

    /**
     * Sets whether the user has completed the onboarding process.
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean)
}


package com.google.samples.apps.nowinandroid.core.model.data

/**
 * A [topic] with the additional information for whether or not it is followed.
 */
// TODO consider changing to UserTopic and flattening
data class FollowableTopic(
    val topic: Topic,
    val isFollowed: Boolean,
)


package com.google.samples.apps.nowinandroid.core.model.data

/**
 * External data layer representation of a NiA Topic
 */
data class Topic(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val url: String,
    val imageUrl: String,
)

package com.google.samples.apps.nowinandroid.core.model.data

/**
 * Class summarizing user interest data
 */
data class UserData(
    val bookmarkedNewsResources: Set<String>,
    val viewedNewsResources: Set<String>,
    val followedTopics: Set<String>,
    val themeBrand: ThemeBrand,
    val darkThemeConfig: DarkThemeConfig,
    val useDynamicColor: Boolean,
    val shouldHideOnboarding: Boolean,
)
```