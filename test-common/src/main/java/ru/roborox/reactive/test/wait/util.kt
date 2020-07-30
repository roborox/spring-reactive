package ru.roborox.reactive.test.wait

fun waitAssert(runnable: () -> Unit) {
    waitAssert(runnable, 5000L)
}

fun waitAssert(runnable: () -> Unit, timeout: Long) {
    val maxTime = System.currentTimeMillis() + timeout
    while (true) {
        try {
            runnable()
            return
        } catch (var8: KotlinNullPointerException) {
            if (System.currentTimeMillis() > maxTime) {
                throw var8
            }
            try {
                Thread.sleep(500L)
            } catch (var7: InterruptedException) {
            }
        } catch (var8: AssertionError) {
            if (System.currentTimeMillis() > maxTime) {
                throw var8
            }
            try {
                Thread.sleep(500L)
            } catch (var7: InterruptedException) {
            }
        }
    }
}