package com.coladay.reservation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Counter {

  private volatile int offset;

  private final int limit;

  private final Lock lock;

  Counter(int limit) {
    this(0, limit);
  }

  Counter(int offset, int limit) {
    this.offset = offset;
    this.limit = limit;
    this.lock = new ReentrantLock();
  }

  boolean increment() {
    lock.lock();
    boolean result = false;
    if (offset < limit) {
      offset ++;
      result = true;
    }
    lock.unlock();
    return result;
  }

  boolean decrement() {
    lock.lock();
    boolean result = false;
    if (offset > 0) {
      offset--;
      result = true;
    }
    lock.unlock();
    return result;
  }

  int getOffset() {
    lock.lock();
    int value = offset;
    lock.unlock();
    return value;
  }
}
