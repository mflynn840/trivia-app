package com.example.co_opapp.Service

import android.util.Log
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

/**
 * WebSocketClientManager
 *
 * A reusable STOMP-over-WebSocket client wrapper for Android.
 *
 * Responsibilities:
 *  - Connects and disconnects from a WebSocket backend.
 *  - Tracks connection lifecycle and exposes `isConnected` state.
 *  - Sends arbitrary payloads to STOMP destinations (topics/endpoints) as JSON.
 *  - Subscribes to STOMP topics and parses incoming JSON into typed objects.
 *
 * This class abstracts the boilerplate of STOMP messaging and RxJava subscriptions,
 * allowing frontend services to expose clean, UI-consumable APIs for real-time updates.
 *
 * Example usage:
 *  val wsManager = WebSocketClientManager("ws://localhost:8080/ws")
 *  wsManager.connect { /* ready */ }
 *  wsManager.send("/app/some-destination", payload)
 *  wsManager.subscribe("/topic/some-topic", MyData::class.java) { data ->
 *      // handle update
 *  }
 */


class WebSocketClientManager(private val backendUrl: String) {
    private val TAG = "WebSocketClientManager"
    private val gson = Gson()
    private val disposables = CompositeDisposable()
    private var stompClient: StompClient? = null
    var isConnected = false
        private set

    fun connect(onOpen: () -> Unit = {}) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, backendUrl)
        stompClient?.lifecycle()?.subscribe { event: LifecycleEvent ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d(TAG, "Connection opened")
                    isConnected = true
                    onOpen()
                }
                LifecycleEvent.Type.CLOSED,
                LifecycleEvent.Type.ERROR,
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                    Log.w(TAG, "Connection closed/error/heartbeat failed")
                    isConnected = false
                }
            }
        }?.let { disposables.add(it) }
        stompClient?.connect()
    }

    fun disconnect() {
        disposables.clear()
        stompClient?.disconnect()
        isConnected = false
    }

    fun send(destination: String, payload: Any) {
        if (!isConnected) return
        val json = gson.toJson(payload)
        stompClient?.send(destination, json)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({}, { Log.e(TAG, "Error sending message", it) })
            ?.let { disposables.add(it) }
    }

    fun <T> subscribe(topic: String, type: Class<T>, onMessage: (T) -> Unit) {
        if (!isConnected) return
        stompClient?.topic(topic)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ frame ->
                val obj = gson.fromJson(frame.payload, type)
                onMessage(obj)
            }, { Log.e(TAG, "Error subscribing to $topic", it) })
            ?.let { disposables.add(it) }
    }
}
