package br.com.mochila.data

import br.com.mochila.model.EventCategory
import br.com.mochila.model.TaskCategory
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class CategoryCachePersistenceTest {

    private lateinit var tempDir: File

    @Before
    fun setUp() {
        tempDir = File.createTempFile("mochila-cat-test", null)
        tempDir.delete()
        tempDir.mkdirs()
        AppDataDir.setPathForTests(tempDir.absolutePath)
        EventCategoryCache.resetForTests()
        TaskCategoryCache.resetForTests()
    }

    @After
    fun tearDown() {
        EventCategoryCache.resetForTests()
        TaskCategoryCache.resetForTests()
        AppDataDir.clearTestPath()
        tempDir.deleteRecursively()
    }

    @Test
    fun `event category persiste entre reinicios do cache`() {
        EventCategoryCache.set(10, EventCategory.SEMINARIO)
        EventCategoryCache.resetForTests()

        assertEquals(EventCategory.SEMINARIO, EventCategoryCache.get(10))
    }

    @Test
    fun `task category persiste entre reinicios do cache`() {
        TaskCategoryCache.set(20, TaskCategory.FICHAMENTO)
        TaskCategoryCache.resetForTests()

        assertEquals(TaskCategory.FICHAMENTO, TaskCategoryCache.get(20))
    }

    @Test
    fun `remove event category atualiza arquivo`() {
        EventCategoryCache.set(5, EventCategory.PROVA)
        EventCategoryCache.remove(5)
        EventCategoryCache.resetForTests()

        assertEquals(EventCategory.default, EventCategoryCache.get(5))
    }
}
