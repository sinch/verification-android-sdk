package com.sinch.utils

/**
 * General interface used by factor pattern to create instances of [T] objects.
 * @param T Type of object that is created.
 */
interface Factory<T> {
    fun create(): T
}