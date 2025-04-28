package com.itza2k.kore.util

import java.util.UUID as JavaUUID

actual object UUID {
    actual fun randomUUID(): String = JavaUUID.randomUUID().toString()
}