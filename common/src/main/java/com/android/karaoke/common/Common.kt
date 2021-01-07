package com.android.karaoke.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers




fun <T> Observable<T>.onUI(): Observable<T> = observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.onUI(onNext: (T) -> Unit): Disposable =
    observeOn(AndroidSchedulers.mainThread()).subscribe(onNext)

fun <T> Observable<T>.onUI(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable =
    observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError)

fun <T> Observable<T>.onIO(): Observable<T> = subscribeOn(Schedulers.io())

fun <T> Single<T>.onIO(): Single<T> = subscribeOn(Schedulers.io())

fun <T> Single<T>.onUI(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable =
    observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError)

fun <T> Maybe<T>.onIO(): Maybe<T> = subscribeOn(Schedulers.io())

fun <T> Maybe<T>.onUI(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable =
    observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError)

fun <T> Flowable<T>.onIO(): Flowable<T> = subscribeOn(Schedulers.io())

fun <T> Flowable<T>.onUI(onNext: (T) -> Unit, onError: (Throwable) -> Unit): Disposable =
    observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError)
