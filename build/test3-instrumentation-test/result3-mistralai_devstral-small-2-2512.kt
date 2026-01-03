```kotlin
package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.test.ext.junit.runners.AndroidxTestExtJunit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@RunWith(AndroidxTestExtJunit4::class)
class TopicDaoTest : DatabaseTest() {

    private val testTopic1 = TopicEntity(
        id = "1",
        name = "Test Topic 1",
        shortDescription = "Short desc 1",
        longDescription = "Long desc 1",
        url = "https://test1.com",
        imageUrl = "https://image1.com"
    )

    private val testTopic2 = TopicEntity(
        id = "2",
        name = "Test Topic 2",
        shortDescription = "Short desc 2",
        longDescription = "Long desc 2",
        url = "https://test2.com",
        imageUrl = "https://image2.com"
    )

    private val testTopic3 = TopicEntity(
        id = "3",
        name = "Test Topic 3",
        shortDescription = "Short desc 3",
        longDescription = "Long desc 3",
        url = "https://test3.com",
        imageUrl = "https://image3.com"
    )

    private suspend fun insertTestTopics(vararg topics: TopicEntity) {
        topicDao.insertOrIgnoreTopics(topics.toList())
    }

    @Test
    fun getTopicEntity_byId_returnsCorrectFlow() = runTest {
        val expected = testTopic1
        insertTestTopics(expected)

        val actual = topicDao.getTopicEntity(expected.id).first()
        assertEquals(expected, actual)
    }

    @Test
    fun getTopicEntities_flowReturnsAllInserted() = runTest {
        val expected = listOf(testTopic1, testTopic2)
        insertTestTopics(*expected.toTypedArray())

        val actual = topicDao.getTopicEntities().first()
        assertEquals(expected, actual)
    }

    @Test
    fun getOneOffTopicEntities_returnsAllInserted() = runTest {
        val expected = listOf(testTopic1, testTopic2)
        insertTestTopics(*expected.toTypedArray())

        val actual = topicDao.getOneOffTopicEntities()
        assertEquals(expected, actual)
    }

    @Test
    fun getTopicEntities_byIds_returnsFilteredFlow() = runTest {
        val allTopics = listOf(testTopic1, testTopic2, testTopic3)
        insertTestTopics(*allTopics.toTypedArray())

        val expected = listOf(testTopic1, testTopic3)
        val actual = topicDao.getTopicEntities(setOf("1", "3")).first()
        assertEquals(expected, actual)
    }

    @Test
    fun insertOrIgnoreTopics_ignoresExisting() = runTest {
        insertTestTopics(testTopic1)

        val duplicate = testTopic1.copy(name = "Modified")
        topicDao.insertOrIgnoreTopics(listOf(duplicate))

        val result = topicDao.getOneOffTopicEntities()
        assertEquals(testTopic1, result.first())
    }

    @Test
    fun upsertTopics_updatesExisting() = runTest {
        insertTestTopics(testTopic1)

        val updated = testTopic1.copy(name = "Updated Name")
        topicDao.upsertTopics(listOf(updated))

        val result = topicDao.getOneOffTopicEntities()
        assertEquals(updated, result.first())
    }

    @Test
    fun deleteTopics_byIds_removesCorrectEntries() = runTest {
        insertTestTopics(testTopic1, testTopic2, testTopic3)

        topicDao.deleteTopics(listOf("1", "3"))

        val result = topicDao.getOneOffTopicEntities()
        assertEquals(listOf(testTopic2), result)
    }
}
```