package com.pranay.currencyconverter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EagerTest {
    val scheduler = TestCoroutineScheduler()
    val testDispatcher = StandardTestDispatcher(scheduler)

    @Test
    fun `eager scheduling test`() = runTest(testDispatcher) {
        var out = true
        launch(testDispatcher) {
            out = false
        }
        testScheduler.runCurrent()
        Assert.assertEquals(false, out)
    }

    @Test
    fun `eager scheduling test2`() {
        var out = true
        runTest(testDispatcher) {
            launch(testDispatcher) {
                out = false
            }
        }
        Assert.assertEquals(false, out)
    }

    @Test
    fun `inner async throws error to outer launch`() {
        runTest(testDispatcher + SupervisorJob()) {
            val outer = async {
                val def = async {
                    println("Entering async block")
                    test()
                }
            }
        }
        //testDispatcher.scheduler.runCurrent()
    }

    suspend fun test(): String {
        val varib = true
        return if (varib) {
            println("Entering ev block")
            throw RuntimeException("vrvr")
        } else
            "vrvr"
    }
}