package ru.touchin.roboswag.components.extensions

import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Simple observable delegate only for notification of new value.
 */
inline fun <T> Delegates.observable(
        initialValue: T,
        crossinline onChange: (newValue: T) -> Unit
): ReadWriteProperty<Any?, T> = object : ObservableProperty<T>(initialValue) {
    override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = onChange(newValue)
}
