package co.tuister.uisers

import io.mockk.mockk

inline fun <reified T : Any> relaxedMockk(block: T.() -> Unit = {}): T {
    return mockk(relaxed = true, relaxUnitFun = true, block = block)
}
