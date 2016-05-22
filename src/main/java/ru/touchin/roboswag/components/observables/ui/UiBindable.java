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

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.Subscriber;

/**
 * Created by Gavriil Sitnikov on 15/04/16.
 * Interface that should be implemented by lifecycle-based elements ({@link android.app.Activity}, {@link android.support.v4.app.Fragment} etc.)
 * to not manually manage subscriptions.
 * Use {@link #bind(Observable, Action1)} method to subscribe to observable onStart and unsubscribe onStop automatically.
 * Use {@link #untilStop(Observable)} method to subscribe to observable where you want and unsubscribe onStop.
 * Use {@link #untilDestroy(Observable)} method to subscribe to observable where you want and unsubscribe onDestroy.
 */
public interface UiBindable {

    /**
     * Method should be used to subscribe to observable while this element is in started state.
     * Passed observable should NOT emit errors. It is illegal as in that case you it stops emitting items and binding lost after error.
     * If you want to process errors return something via {@link Observable#onErrorReturn(Func1)} method and process in onNextAction.
     *
     * @param observable   {@link Observable} to subscribe while started;
     * @param onNextAction Action which will raise on every {@link Subscriber#onNext(Object)} item;
     * @param <T>          Type of emitted by observable items;
     * @return {@link Subscription} which is represents binding. If you want to stop binding then just call {@link Subscription#unsubscribe()} on it.
     */
    @NonNull
    <T> Subscription bind(@NonNull Observable<T> observable, @NonNull Action1<T> onNextAction);

    /**
     * Method should be used to guarantee that observable won't be subscribed after onStop.
     * It is not automatically subscribing on some event so you should manually call {@link Observable#subscribe()} to subscribe to it.
     * Usually it is using to stop requests/execution while element is off or to not do illegal actions after onStop like fragment's stack changing.
     * Don't forget to process errors if observable can emit them.
     *
     * @param observable {@link Observable} to subscribe until onStop;
     * @param <T>        Type of emitted by observable items;
     * @return {@link Observable} which is wrapping source observable to unsubscribe from it onStop.
     */
    @NonNull
    <T> Observable<T> untilStop(@NonNull Observable<T> observable);

    /**
     * Method should be used to guarantee that observable won't be subscribed after onDestroy.
     * It is not automatically subscribing on some event so you should manually call {@link Observable#subscribe()} to subscribe to it.
     * Don't forget to process errors if observable can emit them.
     *
     * @param observable {@link Observable} to subscribe until onDestroy;
     * @param <T>        Type of emitted by observable items;
     * @return {@link Observable} which is wrapping source observable to unsubscribe from it onDestroy.
     */
    @NonNull
    <T> Observable<T> untilDestroy(@NonNull Observable<T> observable);

}