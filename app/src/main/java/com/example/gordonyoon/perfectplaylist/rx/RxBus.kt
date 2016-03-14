package com.example.gordonyoon.perfectplaylist.rx

import rx.Observable
import rx.lang.kotlin.PublishSubject

class RxBus {
    private val mBus = PublishSubject<Any>()

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