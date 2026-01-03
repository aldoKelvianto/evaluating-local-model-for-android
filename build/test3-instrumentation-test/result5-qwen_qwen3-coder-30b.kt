```kotlin
package com.google.samples.apps.nowinandroid.core.database.dao

import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TopicDaoTest : DatabaseTest() {

    private fun createTopicEntity(id: String) = TopicEntity(
        id = id,
        name = "Topic $id",
        shortDescription = "Short description for topic $id",
        longDescription = "Long description for topic $id",
        url = "https://example.com/topic/$id",
        imageUrl = "https://example.com/image/$id.png"
    )

    private suspend fun insertTestTopics(vararg topics: TopicEntity) {
        topicDao.insertOrIgnoreTopics(topics.toList())
    }

    @Test
    fun getTopicEntity_queryByExistingId_returnsTopicEntity() = runTest {
        val topic = createTopicEntity("1")
        insertTestTopics(topic)

        val result = topicDao.getTopicEntity("1").first()

        assertEquals(topic, result)
    }

    @Test
    fun getTopicEntities_queryAll_returnsAllTopicEntities() = runTest {
        val topic1 = createTopicEntity("1")
        val topic2 = createTopicEntity("2")
        insertTestTopics(topic1, topic2)

        val result = topicDao.getTopicEntities().first()

        assertEquals(2, result.size)
        assertEquals(topic1, result[0])
        assertEquals(topic2, result[1])
    }

    @Test
    fun getOneOffTopicEntities_queryAll_returnsAllTopicEntities() = runTest {
        val topic1 = createTopicEntity("1")
        val topic2 = createTopicEntity("2")
        insertTestTopics(topic1, topic2)

        val result = topicDao.getOneOffTopicEntities()

        assertEquals(2, result.size)
        assertEquals(topic1, result[0])
        assertEquals(topic2, result[1])
    }

    @Test
    fun getTopicEntities_queryByIds_returnsMatchingTopicEntities() = runTest {
        val topic1 = createTopicEntity("1")
        val topic2 = createTopicEntity("2")
        val topic3 = createTopicEntity("3")
        insertTestTopics(topic1, topic2, topic3)

        val result = topicDao.getTopicEntities(setOf("1", "3")).first()

        assertEquals(2, result.size)
        assertEquals(topic1, result[0])
        assertEquals(topic3, result[1])
    }

    @Test
    fun insertOrIgnoreTopics_insertNewTopics_insertsSuccessfully() = runTest {
        val topic1 = createTopicEntity("1")
        val topic2 = createTopicEntity("2")

        val result = topicDao.insertOrIgnoreTopics(listOf(topic1, topic2))

        assertEquals(2, result.size)
        assertEquals(0L, result[0])
        assertEquals(0L, result[1])
    }

    @Test
    fun insertOrIgnoreTopics_insertExistingTopics_ignoresDuplicates() = runTest {
        val topic1 = createTopicEntity("1")
        insertTestTopics(topic1)

        val result = topicDao.insertOrIgnoreTopics(listOf(topic1))

        assertEquals(1, result.size)
        assertEquals(-1L, result[0])
    }

    @Test
    fun upsertTopics_insertNewTopics_insertsSuccessfully() = runTest {
        val topic1 = createTopicEntity("1")
        val topic2 = createTopicEntity("2")

        topicDao.upsertTopics(listOf(topic1, topic2))

        val result = topicDao.getOneOffTopicEntities()
        assertEquals(2, result.size)
        assertEquals(topic1, result[0])
        assertEquals(topic2, result[1])
    }

    @Test
    fun upsertTopics_updateExistingTopics_updatesSuccessfully() = runTest {
        val originalTopic = createTopicEntity("1")
        insertTestTopics(originalTopic)

        val updatedTopic = originalTopic.copy(name = "Updated Topic Name")
        topicDao.upsertTopics(listOf(updatedTopic))

        val result = topicDao.getOneOffTopicEntities()
        assertEquals(1, result.size)
        assertEquals(updatedTopic, result[0])
    }

    @Test
    fun deleteTopics_deleteByIds_removesCorrectEntries() = runTest {
        val topic1 = createTopicEntity("1")
        val topic2 = createTopicEntity("2")
        val topic3 = createTopicEntity("3")
        insertTestTopics(topic1, topic2, topic3)

        topicDao.deleteTopics(listOf("1", "3"))

        val result = topicDao.getOneOffTopicEntities()
        assertEquals(1, result.size)
        assertEquals(topic2, result[0])
    }

    @Test
    fun getTopicEntity_queryByNonExistingId_returnsEmptyFlow() = runTest {
        val result = topicDao.getTopicEntity("nonexistent").first()

        assertEquals(null, result)
    }
}
```