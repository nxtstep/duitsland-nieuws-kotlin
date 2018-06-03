package io.supersimple.duitslandnieuws.data.repositories

import org.junit.Test
import java.util.ArrayList
import java.util.HashMap

class SimpleMemCacheTest {
    data class TestObject(
            val id: String,
            val value: String
    )

    class TestCache(map: MutableMap<String, TestObject>) : SimpleMemCache<String, TestObject>(map) {
        override fun getId(value: TestObject): String {
            return value.id
        }
    }

    @Test
    fun testSimpleMemCache() {
        val cache: SimpleMemCache<String, TestObject> = TestCache(HashMap<String, TestObject>())

        //
        // Save
        cache.save(TestObject("1", "Test me"))
                .test()
                .assertResult(TestObject("1", "Test me"))

        //
        // Get
        cache.get("1")
                .test()
                .assertResult(TestObject("1", "Test me"))

        cache.get("2")
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()

        cache.list(0, 1)
                .test()
                .assertResult(listOf(TestObject("1", "Test me")))

        cache.list(0, 10)
                .test()
                .assertNoValues()
                .assertComplete()
                .assertNoErrors()

        //
        // Delete
        cache.delete("2")
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()

        cache.delete("1")
                .test()
                .assertResult(TestObject("1", "Test me"))

        cache.delete(TestObject("1", "Test me"))
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()

        //
        // Clear
        cache.save(TestObject("2", "Whatever value"))
                .test()
                .assertValueCount(1)
                .assertNoErrors()
                .assertComplete()

        cache.clear()

        cache.list(0, 1)
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun testBulkSaveAndDelete() {
        val cache: SimpleMemCache<String, TestObject> = TestCache(HashMap<String, TestObject>())

        //
        // Bulk save
        val object1 = TestObject("1", "Object 1")
        val object2 = TestObject("2", "Object 2")
        val object3 = TestObject("3", "Object 3")

        val objects = listOf(object1, object2, object3)
        cache.save(objects)
                .test()
                .assertResult(objects)

        cache.delete(object2)
                .test()
                .assertResult(object2)

        cache.get("2")
                .test()
                .assertNoValues()
                .assertNoErrors()
                .assertComplete()

        cache.deleteAll()
                .test()
                .assertResult(listOf(object1, object3))

        // Make sure deleting an empty cache doesn't return a value in the stream
        cache.deleteAll()
                .test()
                .assertValueCount(1)
                .assertNoErrors()
                .assertComplete()

        // Saving empty List should complete without error
        cache.save(ArrayList<TestObject>())
                .test()
                .assertNoErrors()
                .assertComplete()
    }
}