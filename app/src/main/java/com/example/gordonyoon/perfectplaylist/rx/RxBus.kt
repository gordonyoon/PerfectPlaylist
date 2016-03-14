package com.example.gordonyoon.perfectplaylist.rx

import rx.Observable
import rx.subjects.PublishSubject

class RxBus {
    private val mBus = PublishSubject.create<Any>()

    fun send(o: Any) {
        mBus.onNext(o)
    }

    fun toObserverable(): Observable<Any> {
        return mBus
    }

    fun hasObservers(): Boolean {
        return mBus.hasObservers()
    }
}