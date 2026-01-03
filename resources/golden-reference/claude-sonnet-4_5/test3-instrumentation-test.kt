package com.google.samples.apps.nowinandroid.core.database.dao

import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TopicDaoTest : DatabaseTest() {

    @Test
    fun getTopicEntities_emptyDatabase_returnsEmptyList() = runTest {
        val topics = topicDao.getTopicEntities().first()
        assertEquals(0, topics.size)
    }

    @Test
    fun getTopicEntities_afterInsertion_returnsInsertedTopics() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2"),
            createTestTopicEntity(id = "3")
        )
        insertTestTopics(testTopics)

        val topics = topicDao.getTopicEntities().first()
        assertEquals(3, topics.size)
        assertEquals(testTopics, topics)
    }

    @Test
    fun getOneOffTopicEntities_afterInsertion_returnsInsertedTopics() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2")
        )
        insertTestTopics(testTopics)

        val topics = topicDao.getOneOffTopicEntities()
        assertEquals(2, topics.size)
        assertEquals(testTopics, topics)
    }

    @Test
    fun getTopicEntity_byId_returnsCorrectTopic() = runTest {
        val testTopic = createTestTopicEntity(id = "1", name = "Test Topic")
        insertTestTopics(listOf(testTopic))

        val topic = topicDao.getTopicEntity("1").first()
        assertEquals(testTopic, topic)
    }

    @Test
    fun getTopicEntities_byIds_returnsFilteredTopics() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2"),
            createTestTopicEntity(id = "3")
        )
        insertTestTopics(testTopics)

        val topics = topicDao.getTopicEntities(setOf("1", "3")).first()
        assertEquals(2, topics.size)
        assertEquals(testTopics[0], topics[0])
        assertEquals(testTopics[2], topics[1])
    }

    @Test
    fun getTopicEntities_byEmptyIds_returnsEmptyList() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2")
        )
        insertTestTopics(testTopics)

        val topics = topicDao.getTopicEntities(emptySet()).first()
        assertEquals(0, topics.size)
    }

    @Test
    fun insertOrIgnoreTopics_newTopics_returnsInsertedRowIds() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2")
        )

        val rowIds = topicDao.insertOrIgnoreTopics(testTopics)
        assertEquals(2, rowIds.size)
        assertEquals(true, rowIds.all { it > 0 })
    }

    @Test
    fun insertOrIgnoreTopics_duplicateTopics_ignoresExisting() = runTest {
        val testTopic = createTestTopicEntity(id = "1", name = "Original")
        insertTestTopics(listOf(testTopic))

        val duplicateTopic = createTestTopicEntity(id = "1", name = "Duplicate")
        val rowIds = topicDao.insertOrIgnoreTopics(listOf(duplicateTopic))
        assertEquals(1, rowIds.size)
        assertEquals(-1, rowIds[0])

        val topic = topicDao.getTopicEntity("1").first()
        assertEquals("Original", topic.name)
    }

    @Test
    fun upsertTopics_newTopics_insertsSuccessfully() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2")
        )

        topicDao.upsertTopics(testTopics)

        val topics = topicDao.getTopicEntities().first()
        assertEquals(2, topics.size)
        assertEquals(testTopics, topics)
    }

    @Test
    fun upsertTopics_existingTopics_updatesSuccessfully() = runTest {
        val originalTopic = createTestTopicEntity(id = "1", name = "Original")
        insertTestTopics(listOf(originalTopic))

        val updatedTopic = createTestTopicEntity(id = "1", name = "Updated")
        topicDao.upsertTopics(listOf(updatedTopic))

        val topic = topicDao.getTopicEntity("1").first()
        assertEquals("Updated", topic.name)
    }

    @Test
    fun upsertTopics_mixedNewAndExisting_upsertsCorrectly() = runTest {
        val existingTopic = createTestTopicEntity(id = "1", name = "Existing")
        insertTestTopics(listOf(existingTopic))

        val updatedTopic = createTestTopicEntity(id = "1", name = "Updated")
        val newTopic = createTestTopicEntity(id = "2", name = "New")
        topicDao.upsertTopics(listOf(updatedTopic, newTopic))

        val topics = topicDao.getTopicEntities().first()
        assertEquals(2, topics.size)
        assertEquals("Updated", topics[0].name)
        assertEquals("New", topics[1].name)
    }

    @Test
    fun deleteTopics_byIds_removesCorrectTopics() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2"),
            createTestTopicEntity(id = "3")
        )
        insertTestTopics(testTopics)

        topicDao.deleteTopics(listOf("1", "3"))

        val remainingTopics = topicDao.getTopicEntities().first()
        assertEquals(1, remainingTopics.size)
        assertEquals("2", remainingTopics[0].id)
    }

    @Test
    fun deleteTopics_emptyIds_deletesNothing() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2")
        )
        insertTestTopics(testTopics)

        topicDao.deleteTopics(emptyList())

        val topics = topicDao.getTopicEntities().first()
        assertEquals(2, topics.size)
    }

    @Test
    fun deleteTopics_nonExistingIds_deletesNothing() = runTest {
        val testTopics = listOf(
            createTestTopicEntity(id = "1"),
            createTestTopicEntity(id = "2")
        )
        insertTestTopics(testTopics)

        topicDao.deleteTopics(listOf("3", "4"))

        val topics = topicDao.getTopicEntities().first()
        assertEquals(2, topics.size)
    }

    private suspend fun insertTestTopics(topics: List<TopicEntity>) {
        topicDao.insertOrIgnoreTopics(topics)
    }

    private fun createTestTopicEntity(
        id: String,
        name: String = "Test Topic $id",
        shortDescription: String = "Short description $id",
        longDescription: String = "Long description $id",
        url: String = "https://example.com/$id",
        imageUrl: String = "https://example.com/image/$id"
    ) = TopicEntity(
        id = id,
        name = name,
        shortDescription = shortDescription,
        longDescription = longDescription,
        url = url,
        imageUrl = imageUrl
    )
}