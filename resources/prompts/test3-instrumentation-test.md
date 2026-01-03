Context: Android developer using Kotlin and Room database testing libraries for testing Android DAO code

Task: Generate unit tests for the provided DAO below

Requirements:
Create unit test methods covering these scenarios:
1. Query operations - Verify DAO retrieves entities correctly (both Flow and one-off queries)
2. Insert operations - Verify insert behavior (insertOrIgnore strategy)
3. Update operations - Verify upsert behavior updates existing entries
4. Delete operations - Verify deletion by ID removes correct entries
5. Query by ID - Verify filtering entities by specific IDs

Constraints:
- Extend the DatabaseTest base class for database setup
- Don't use mocking library (or introduce any new libraries)
- Use meaningful test method names following the pattern: methodName_scenario_expectedBehavior
- Include proper assertions using kotlin.test.assertEquals
- Use runTest for coroutine testing
- Include all necessary imports and dependencies
- Include helper functions for test data setup
- Use the attached source code as reference
- The output code is a new file under the test folder

Expected output:
- Only valid Kotlin code in plain text format — no comments, explanations, markdown, backticks, or any other non-code content.
- Class name should follow the pattern: [EntityName]DaoTest
- Include private helper functions for inserting test data and creating test entities
  Source Code:

```kotlin
package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [TopicEntity] access
 */
@Dao
interface TopicDao {
    @Query(
        value = """
SELECT * FROM topics
        WHERE id = :topicId
""",
    )
    fun getTopicEntity(topicId: String): Flow<TopicEntity>

    @Query(value = "SELECT * FROM topics")
    fun getTopicEntities(): Flow<List<TopicEntity>>

    @Query(value = "SELECT * FROM topics")
    suspend fun getOneOffTopicEntities(): List<TopicEntity>

    @Query(
        value = """
SELECT * FROM topics
        WHERE id IN (:ids)
""",
    )
    fun getTopicEntities(ids: Set<String>): Flow<List<TopicEntity>>

    /**
     * Inserts [topicEntities] into the db if they don't exist, and ignores those that do
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopics(topicEntities: List<TopicEntity>): List<Long>

    /**
     * Inserts or updates [entities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertTopics(entities: List<TopicEntity>)

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
DELETE FROM topics
WHERE id in (:ids)
""",
    )
    suspend fun deleteTopics(ids: List<String>)
}

package com.google.samples.apps.nowinandroid.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import org.junit.After
import org.junit.Before

internal abstract class DatabaseTest {

    private lateinit var db: NiaDatabase
    protected lateinit var newsResourceDao: NewsResourceDao
    protected lateinit var topicDao: TopicDao

    @Before
    fun setup() {
        db = run {
            val context = ApplicationProvider.getApplicationContext<Context>()
            Room.inMemoryDatabaseBuilder(
                context,
                NiaDatabase::class.java,
            ).build()
        }
        newsResourceDao = db.newsResourceDao()
        topicDao = db.topicDao()
    }

    @After
    fun teardown() = db.close()
}


package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.samples.apps.nowinandroid.core.model.data.Topic

/**
 * Defines a topic a user may follow.
 * It has a many to many relationship with [NewsResourceEntity]
 */
@Entity(
    tableName = "topics",
)
data class TopicEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val shortDescription: String,
    @ColumnInfo(defaultValue = "")
    val longDescription: String,
    @ColumnInfo(defaultValue = "")
    val url: String,
    @ColumnInfo(defaultValue = "")
    val imageUrl: String,
)

fun TopicEntity.asExternalModel() = Topic(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    url = url,
    imageUrl = imageUrl,
)
```