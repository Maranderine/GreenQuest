package de.hsb.greenquest.domain.usecase

import de.hsb.greenquest.data.local.entity.LocalChallengeEntity
import de.hsb.greenquest.data.network.PlantNetDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class EventManager@Inject constructor() {
    private var internalEvents: MutableSharedFlow<String> = MutableSharedFlow()
    //protected val eventsInternal: MutableSharedFlow<String> =  MutableSharedFlow<String>("")
    //protected val unconfinedScope = CoroutineScope(Dispatchers.Unconfined)
    val events: SharedFlow<String>
        get() = internalEvents.asSharedFlow()

    init {
        // When subscriptionCount increments from 0 to 1, setup the native hook.
        /*internalEvents.subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged()
            .drop(1) // Drop first false event
            .onEach { if (it) startReadingEvents() else stopReadingEvents() }
            .launchIn(unconfinedScope)*/
        //events.launchIn(unconfinedScope)
    }

    suspend fun sendEvent(s: String){
        println("in event manager !")
        internalEvents.emit(s)
    }
}