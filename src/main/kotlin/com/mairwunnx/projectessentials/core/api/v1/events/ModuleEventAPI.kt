@file:Suppress("unused")

package com.mairwunnx.projectessentials.core.api.v1.events

/**
 * Event API class, contains all methods for interacting
 * with events.
 * @since Mod: 1.14.4-2.0.0, API: 1.0.0
 */
object ModuleEventAPI {
    private val events: HashMap<IModuleEventType, MutableList<(
        IModuleEventData
    ) -> Unit>> = hashMapOf()

    /**
     * Fire target event type. And send some data.
     * @param eventType event type.
     * @param eventData event data.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun fire(
        eventType: IModuleEventType,
        eventData: IModuleEventData
    ) {
        events.keys.forEach {
            if (it == eventType) {
                events[it]?.forEach { method ->
                    method.invoke(eventData)
                }
                return@forEach
            }
        }
    }

    /**
     * Subscribe on target event type.
     * @param T event data data type.
     * @param event target event type.
     * @param action action or method reference what will
     * be called on event firing. Method must return `Unit` or
     * in java it `void`.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> subscribeOn(
        event: IModuleEventType,
        action: (T) -> Unit
    ) {
        val pushedAction = action as (IModuleEventData) -> Unit
        val eventCollection = events[event]

        if (eventCollection == null) {
            events[event] = mutableListOf(pushedAction)
        } else {
            eventCollection.add(pushedAction)
        }
    }

    /**
     * @return all subscribed events.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getAllEvents() = events

    /**
     * @param eventType target event type.
     * @return mutable list with references on subscribed methods.
     * @throws EventNotFoundException when event type not found.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun getEventsByType(
        eventType: IModuleEventType
    ): MutableList<(IModuleEventData) -> Unit> {
        events.keys.forEach {
            if (it == eventType) {
                getAllEvents()[it]?.let { methods ->
                    return methods
                }
            }
        }

        throw EventNotFoundException()
    }

    /**
     * Removing all method references for specified event type.
     * @param eventType target event type.
     * @throws EventNotFoundException when event type not found.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun killEventsByType(eventType: IModuleEventType) {
        events.keys.forEach {
            if (it == eventType) {
                getAllEvents()[it]?.clear()
                return
            }
        }
        throw EventNotFoundException()
    }

    /**
     * Removing all methods references from all event types.
     * @since Mod: 1.14.4-2.0.0, API: 1.0.0
     */
    fun killAllEvents() = events.clear()
}
