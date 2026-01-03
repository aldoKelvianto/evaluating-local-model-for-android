```kotlin
package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class TopicDaoTest : DatabaseTest() {

    private val testTopic1 = createTestTopic("1", "Kotlin")
    private val testTopic2 = createTestTopic("2", "Android")

    @Before
    override fun setup() {
        super.setup()
        runTest { topicDao.insertOrIgnoreTopics(listOf(testTopic1, testTopic2)) }
    }

    @Test
    fun getTopicEntity_FlowQuery_ReturnsCorrectEntity() = runTest {
        val result = topicDao.getTopicEntity("1").first()
        assertEquals(testTopic1, result)
    }

    @Test
    fun getOneOffTopicEntities_OneOffQuery_ReturnsAllEntities() = runTest {
        val result = topicDao.getOneOffTopicEntities()
        assertTrue(result.contains(testTopic1))
        assertTrue(result.contains(testTopic2))
    }

    @Test
    fun insertOrIgnoreTopics_InsertNewEntity_ReturnsInsertedIds() = runTest {
        val newTopic = createTestTopic("3", "Coroutines")
        val result = topicDao.insertOrIgnoreTopics(listOf(newTopic))
        assertEquals(1, result.size)
        assertTrue(result.contains(3L)) // Assuming auto-increment ID
    }

    @Test
    fun upsertTopics_UpdateExistingEntity_UpdatesCorrectly() = runTest {
        val updatedTopic = testTopic1.copy(name = "Kotlin Updated")
        topicDao.upsertTopics(listOf(updatedTopic))
        val result = topicDao.getOneOffTopicEntities()
        assertEquals("Kotlin Updated", result.find { it.id == "1" }?.name)
    }

    @Test
    fun deleteTopics_DeleteById_RemovesCorrectEntity() = runTest {
        topicDao.deleteTopics(listOf("1"))
        val result = topicDao.getOneOffTopicEntities()
        assertTrue(result.none { it.id == "1" })
    }

    @Test
    fun getTopicEntities_FilterByIDs_ReturnsFilteredEntities() = runTest {
        val result = topicDao.getTopicEntities(setOf("1", "2")).first()
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == "1" })
        assertTrue(result.any { it.id == "2" })
    }

    private fun createTestTopic(id: String, name: String): TopicEntity {
        return TopicEntity(
            id = id,
            name = name,
            shortDescription = "$name Short",
            longDescription = "$name Long",
            url = "http://example.com/$id",
            imageUrl = "http://example.com/image/$id"
        )
    }
}
```