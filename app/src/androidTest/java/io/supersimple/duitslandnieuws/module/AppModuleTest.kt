package io.supersimple.duitslandnieuws.module

import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito

@Category(AppModuleTest::class)
class AppModuleTest: KoinTest {
    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun checkAppModule() {
        checkModules {
            modules(appModule, cloudModule, diskModule)
        }
    }
}