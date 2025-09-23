package com.example.co_opapp.Service.Backend

import android.content.ContentValues
import android.util.Log
import com.example.co_opapp.data_model.Lobby
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent


/**
 * Manages a STOMP WebSocket connection to the backend server.
 * Handles connection lifecycle, topic subscriptions, and message sending.
 */
class WebSocketClientManager(private val backendUrl: String = "ws://192.168.4.21:8080/ws") {
    private val gson = Gson()
    private val disposables = CompositeDisposable()
    private var stompClient: StompClient? = null
    var isConnected = false
        private set


    /**
     * Establish WebSocket/STOMP connection.
     * @param onOpen Callback fired when connection is successfully opened.
     */
    fun connect(onOpen: () -> Unit = {}) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, backendUrl)

        //subscribe to lifecycle events (open/close/etc)
        stompClient?.lifecycle()?.subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    isConnected = true
                    onOpen()
                }
                LifecycleEvent.Type.CLOSED, LifecycleEvent.Type.ERROR -> {
                    isConnected = false
                }
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> TODO()
            }
        }?.let { disposables.add(it) }
        stompClient?.connect()
    }


    /**
     * Subscribe to the /topic/lobby/all endpoint.
     * The server is expected to return a JSON object mapping lobby names -> Lobby objects.
     */
    fun subscribeLobbyAll(onMessage: (Map<String, Lobby>) -> Unit) {
        val type = object : TypeToken<Map<String, Lobby>>() {}.type
        stompClient?.topic("/topic/lobby/all")
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ frame ->
                // Deserialize with the correct type
                val map = gson.fromJson<Map<String, Lobby>>(frame.payload, type)
                onMessage(map)
            }, { Log.e(ContentValues.TAG, "Error subscribing to /topic/lobby/all", it) })
            ?.let { disposables.add(it) }
    }


    /**
     * Subscribe to updates for a specific lobby.
     * @param lobbyName The lobby identifier.
     * support generic topics
     */
    fun <T> subscribeTopic(destination: String, clazz: Class<T>, onMessage: (T) -> Unit) {
        stompClient?.topic(destination)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ frame ->
                val obj = gson.fromJson(frame.payload, clazz)
                onMessage(obj)
            }, { Log.e(ContentValues.TAG, "Error subscribing to $destination", it) })
            ?.let { disposables.add(it) }
    }

    fun send(destination: String, payload: Any) {
        if (!isConnected) return
        val json = gson.toJson(payload)
        stompClient?.send(destination, json)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({}, { Log.e(ContentValues.TAG, "Error sending message", it) })
            ?.let { disposables.add(it) }
    }

    fun disconnect() {
        stompClient?.disconnect()
        disposables.clear()
        isConnected = false
    }
}