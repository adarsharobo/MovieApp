package com.robosoftin.movieapp.util.flow

import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * To fire events.
 * This flow won't fire the last value for each collect call. 
 * This observer will only be invoked on `tryEmit` calls.
 * (replacement for SingleLiveEvent :D)
 */
fun <T> mutableEventFlow(): MutableSharedFlow<T> {
    return MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1
    )
}
