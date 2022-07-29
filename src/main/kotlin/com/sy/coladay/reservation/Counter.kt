package com.sy.coladay.reservation

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class Counter(offset: Int, limit: Int) {
    private val limit: Int
    private val lock: Lock
    private val offset: AtomicInteger

    constructor(limit: Int) : this(0, limit) {}

    init {
        this.offset = AtomicInteger(offset)
        this.limit = limit
        this.lock = ReentrantLock()
    }

    fun increment(): Boolean {
        lock.lock()
        var result = false
        if (offset.get() < limit) {
            offset.getAndIncrement()
            result = true
        }
        lock.unlock()
        return result;
    }

    fun decrement(): Boolean {
        lock.lock()
        var result = false
        if (offset.get() > 0) {
            offset.getAndDecrement()
            result = true
        }
        lock.unlock()
        return result
    }

    fun getOffset(): Int {
        lock.lock()
        val value = offset.get()
        lock.unlock()
        return value
    }
}