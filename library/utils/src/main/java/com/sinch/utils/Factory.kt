package com.sinch.utils

interface Factory<T> {
    fun create(): T
}