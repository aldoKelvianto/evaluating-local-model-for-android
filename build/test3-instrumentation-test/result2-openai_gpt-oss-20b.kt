package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal abstract class DatabaseTest {

    private lateinit var db: NiaDatabase
    protected lateinit var topicDao: TopicDao

    @BeforeTest
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, NiaDatabase::class.java).build()
        topicDao = db.topicDao()
    }

    @AfterTest
    fun teardown() = db.close()
}

class TopicDaoTest : DatabaseTest() {

    private fun createTopic(id: String, name: String = "Name $id"): TopicEntity =
        TopicEntity(
            id = id,
            name = name,
            shortDescription = "Short $id",
            longDescription = "",
            url = "",
            imageUrl = ""
        )

    private suspend fun insertTopics(vararg topics: TopicEntity) {
        topicDao.insertOrIgnoreTopics(topics.toList())
    }

    @Test
    fun getTopicEntities_flow_returnsAllInserted() = runTest {
        val t1 = createTopic("t1")
        val t2 = createTopic("t2")
        insertTopics(t1, t2)

        val list = topicDao.getTopicEntities().first()
        assertEquals(2, list.size)
        assertEquals(setOf(t1.id, t2.id), list.map { it.id }.toSet())
    }

    @Test
    fun getOneOffTopicEntities_returnsAllInserted() = runBlocking {
        val t1 = createTopic("t1")
        val t2 = createTopic("t2")
        insertTopics(t1, t2)

        val list = topicDao.getOneOffTopicEntities()
        assertEquals(2, list.size)
        assertEquals(setOf(t1.id, t2.id), list.map { it.id }.toSet())
    }

    @Test
    fun insertOrIgnoreTopics_ignoresDuplicates() = runBlocking {
        val t1 = createTopic("t1")
        val t2 = createTopic("t2")
        topicDao.insertOrIgnoreTopics(listOf(t1, t2))

        // Attempt to insert again with same ids but different names
        val duplicateT1 = createTopic("t1", "NewName")
        val duplicateT2 = createTopic("t2", "AnotherName")
        topicDao.insertOrIgnoreTopics(listOf(duplicateT1, duplicateT2))

        val list = topicDao.getOneOffTopicEntities()
        assertEquals(2, list.size)
        // Names should remain original
        assertEquals(t1.name, list.first { it.id == t1.id }.name)
        assertEquals(t2.name, list.first { it.id == t2.id }.name)
    }

    @Test
    fun upsertTopics_updatesExistingEntries() = runBlocking {
        val t1 = createTopic("t1", "OldName")
        insertTopics(t1)

        // Upsert with new name
        val updatedT1 = createTopic("t1", "NewName")
        topicDao.upsertTopics(listOf(updatedT1))

        val list = topicDao.getOneOffTopicEntities()
        assertEquals(1, list.size)
        assertEquals("NewName", list.first().name)
    }

    @Test
    fun deleteTopics_removesSpecifiedIds() = runBlocking {
        val t1 = createTopic("t1")
        val t2 = createTopic("t2")
        val t3 = createTopic("t3")
        insertTopics(t1, t2, t3)

        topicDao.deleteTopics(listOf("t1", "t3"))

        val list = topicDao.getOneOffTopicEntities()
        assertEquals(1, list.size)
        assertEquals("t2", list.first().id)
    }

    @Test
    fun getTopicEntity_byId_returnsCorrectEntity() = runBlocking {
        val t1 = createTopic("t1")
        val t2 = createTopic("t2")
        insertTopics(t1, t2)

        val flow = topicDao.getTopicEntity("t2")
        val entity = flow.first()
        assertEquals(t2.id, entity.id)
        assertEquals(t2.name, entity.name)
    }

    @Test
    fun getTopicEntities_byIds_returnsOnlyMatching() = runBlocking {
        val t1 = createTopic("t1")
        val t2 = createTopic("t2")
        val t3 = createTopic("t3")
        insertTopics(t1, t2, t3)

        val flow = topicDao.getTopicEntities(setOf("t1", "t3"))
        val list = flow.first()
        assertEquals(2, list.size)
        assertEquals(setOf("t1", "t3"), list.map { it.id }.toSet())
    }
}