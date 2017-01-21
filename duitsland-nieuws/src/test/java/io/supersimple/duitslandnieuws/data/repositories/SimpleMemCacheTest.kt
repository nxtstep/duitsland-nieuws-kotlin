package io.supersimple.duitslandnieuws.data.repositories

import io.supersimple.duitslandnieuws.data.repositories.SimpleMemCache
import org.junit.Test
import java.util.*

class SimpleMemCacheTest {
    data class TestObject(
            val id: String,
            val value: String
    )

    class MyCache(val map: MutableMap<String, TestObject>) : SimpleMemCache<TestObject>(map) {
        override fun getId(value: TestObject): String {
            return value.id
        }
    }

    @Test
    fun testSimpleMemCache() {
        val cache: SimpleMemCache<TestObject> = MyCache(HashMap<String, TestObject>())

        //
        // Save
        cache.save(TestObject("1", "Test me"))
                .test()
                .assertNoErrors()
                .assertResult(TestObject("1", "Test me"))
                .assertComplete()

        //
        // Get
        cache.get("1")
                .test()
                .assertResult(TestObject("1", "Test me"))
                .assertComplete()

        cache.get("2")
                .test()
                .assertNoValues()
                .assertComplete()

        cache.list()
                .test()
                .assertResult(Arrays.asList(TestObject("1", "Test me")))
                .assertComplete()

        //
        // Delete
        cache.delete("2")
                .test()
                .assertNoValues()
                .assertComplete()

        cache.delete("1")
                .test()
                .assertResult(TestObject("1", "Test me"))
                .assertComplete()

        cache.delete(TestObject("1", "Test me"))
                .test()
                .assertNoValues()
                .assertComplete()

        //
        // Clear
        cache.save(TestObject("2", "Whatever value"))
                .test()
                .assertNoErrors()
                .assertComplete()

        cache.clear()

        cache.list()
                .test()
                .assertNoValues()
                .assertComplete()
    }

    @Test
    fun testBulkSaveAndDelete() {
        val cache: SimpleMemCache<TestObject> = MyCache(HashMap<String, TestObject>())

        //
        // Bulk save
        val object1 = TestObject("1", "Object 1")
        val object2 = TestObject("2", "Object 2")
        val object3 = TestObject("3", "Object 3")

        val objects = Arrays.asList(object1, object2, object3)
        cache.save(objects)
                .test()
                .assertResult(objects)
                .assertComplete()

        cache.delete(object2)
                .test()
                .assertResult(object2)
                .assertComplete()

        cache.deleteAll()
                .test()
                .assertResult(Arrays.asList(object1, object3))
                .assertComplete()

        // Make sure deleting an empty cache doesn't return a value in the stream
        cache.deleteAll()
                .test()
                .assertNoValues()
                .assertComplete()

        cache.save(ArrayList<TestObject>())
                .test()
                .assertNoValues()
                .assertComplete()
    }
}