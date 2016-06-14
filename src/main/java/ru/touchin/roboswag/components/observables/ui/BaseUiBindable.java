/*
 *  Copyright (c) 2015 RoboSwag (Gavriil Sitnikov, Vsevolod Ivanov)
 *
 *  This file is part of RoboSwag library.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package ru.touchin.roboswag.components.observables.ui;

import android.support.annotation.NonNull;

import ru.touchin.roboswag.core.log.Lc;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.subjects.BehaviorSubject;

/**
 * Created by Gavriil Sitnikov on 18/04/16.
 * Simple implementation of {@link UiBindable}. Could be used to not implement interface but use such object inside.
 */
public class BaseUiBindable implements UiBindable {

    @NonNull
    private final BehaviorSubject<Boolean> isCreatedSubject = BehaviorSubject.create();
    @NonNull
    private final BehaviorSubject<Boolean> isStartedSubject = BehaviorSubject.create();

    /**
     * Call it on parent's onCreate method.
     */
    public void onCreate() {
        isCreatedSubject.onNext(true);
    }

    /**
     * Call it on parent's onStart method.
     */
    public void onStart() {
        isStartedSubject.onNext(true);
    }

    /**
     * Call it on parent's onStop method.
     */
    public void onStop() {
        isStartedSubject.onNext(false);
    }

    /**
     * Call it on parent's onDestroy method.
     */
    public void onDestroy() {
        isCreatedSubject.onNext(false);
    }

    @NonNull
    @Override
    public <T> Subscription bind(@NonNull final Observable<T> observable, @NonNull final Action1<T> onNextAction) {
        final Observable<T> safeObservable = observable
                .onErrorResumeNext(throwable -> {
                    Lc.assertion(throwable);
                    return Observable.never();
                })
                .observeOn(AndroidSchedulers.mainThread());
        return isStartedSubject.switchMap(isStarted -> isStarted ? safeObservable : Observable.never())
                .takeUntil(isCreatedSubject.filter(created -> !created))
                .subscribe(onNextAction);
    }

    @NonNull
    @Override
    public <T> Subscription untilStop(@NonNull final Observable<T> observable) {
        return untilStop(observable, Actions.empty(), Lc::assertion, Actions.empty());
    }

    @NonNull
    @Override
    public <T> Subscription untilStop(@NonNull final Observable<T> observable, @NonNull final Action1<T> onNextAction) {
        return untilStop(observable, onNextAction, Lc::assertion, Actions.empty());
    }

    @NonNull
    @Override
    public <T> Subscription untilStop(@NonNull final Observable<T> observable,
                                      @NonNull final Action1<T> onNextAction, @NonNull final Action1<Throwable> onErrorAction) {
        return untilStop(observable, onNextAction, onErrorAction, Actions.empty());
    }

    @NonNull
    @Override
    public <T> Subscription untilStop(@NonNull final Observable<T> observable,
                                      @NonNull final Action1<T> onNextAction, @NonNull final Action1<Throwable> onErrorAction, @NonNull final Action0 onCompletedAction) {
        return isCreatedSubject.first()
                .switchMap(isCreated -> isCreated ? observable.observeOn(AndroidSchedulers.mainThread()) : Observable.empty())
                .takeUntil(isStartedSubject.filter(started -> !started))
                .subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    @NonNull
    @Override
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable) {
        return untilDestroy(observable, Actions.empty(), Lc::assertion, Actions.empty());
    }

    @NonNull
    @Override
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable, @NonNull final Action1<T> onNextAction) {
        return untilDestroy(observable, onNextAction, Lc::assertion, Actions.empty());
    }

    @NonNull
    @Override
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable,
                                         @NonNull final Action1<T> onNextAction, @NonNull final Action1<Throwable> onErrorAction) {
        return untilDestroy(observable, onNextAction, onErrorAction, Actions.empty());
    }

    @NonNull
    @Override
    public <T> Subscription untilDestroy(@NonNull final Observable<T> observable,
                                         @NonNull final Action1<T> onNextAction, @NonNull final Action1<Throwable> onErrorAction, @NonNull final Action0 onCompletedAction) {
        return isCreatedSubject.first()
                .switchMap(isCreated -> isCreated ? observable.observeOn(AndroidSchedulers.mainThread()) : Observable.empty())
                .takeUntil(isCreatedSubject.filter(created -> !created))
                .subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

}
