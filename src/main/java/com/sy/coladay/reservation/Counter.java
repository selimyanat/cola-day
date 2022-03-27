package com.sy.coladay.reservation;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Counter {

  private final int limit;
  private final Lock lock;
  private AtomicInteger offset;

  Counter(int limit) {
    this(0, limit);
  }

  Counter(int offset, int limit) {
    this.offset = new AtomicInteger(offset);
    this.limit = limit;
    this.lock = new ReentrantLock();
  }

  boolean increment() {
    lock.lock();
    boolean result = false;
    if (offset.get() < limit) {
      offset.getAndIncrement();
      result = true;
    }
    lock.unlock();
    return result;
  }

  boolean decrement() {
    lock.lock();
    boolean result = false;
    if (offset.get() > 0) {
      offset.getAndDecrement();
      result = true;
    }
    lock.unlock();
    return result;
  }

  int getOffset() {
    lock.lock();
    int value = offset.get();
    lock.unlock();
    return value;
  }
}
