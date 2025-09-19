package com.example.co_opapp.Service.Coop

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
import java.lang.reflect.Type
class WebSocketClientManager(private val backendUrl: String = "ws://192.168.4.21:8080/ws") {
    private val TAG = "WebSocketClientManager"
    private val gson = Gson()
    private val disposables = CompositeDisposable()
    private var stompClient: StompClient? = null
    var isConnected = false
        private set

    private val pendingSubscriptions = mutableListOf<() -> Unit>()

    fun connect(onOpen: () -> Unit = {}) {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, backendUrl)
        stompClient?.lifecycle()?.subscribe { event ->
            when (event.type) {
                LifecycleEvent.Type.OPENED -> {
                    isConnected = true
                    pendingSubscriptions.forEach { it() }
                    pendingSubscriptions.clear()
                    onOpen()
                }
                LifecycleEvent.Type.CLOSED,
                LifecycleEvent.Type.ERROR,
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
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
        pendingSubscriptions.clear()
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

    /** Subscribe to a single lobby */
    fun subscribeLobby(topic: String, onMessage: (Lobby) -> Unit) {
        val action = {
            stompClient?.topic(topic)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ frame ->
                    val lobby = gson.fromJson(frame.payload, Lobby::class.java)
                    onMessage(lobby)
                }, { Log.e(TAG, "Error subscribing to $topic", it) })
                ?.let { disposables.add(it) }
        }
        if (isConnected) action() else {
            Log.e(TAG, "Cannot subscribe to /topic/lobby/all: not connected")
        }
    }

    /** Subscribe to all lobbies */
    fun subscribeLobbyAll(onMessage: (Map<String, Lobby>) -> Unit) {
        val type = object : TypeToken<Map<String, Lobby>>() {}.type
        val action = {
            stompClient?.topic("/topic/lobby/all")
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ frame ->
                    val map = gson.fromJson<Map<String, Lobby>>(frame.payload, type)
                    onMessage(map)
                }, { Log.e(TAG, "Error subscribing to /topic/lobby/all", it) })
                ?.let { disposables.add(it) }
        }
        if (isConnected) action() else {
            Log.e(TAG, "Cannot subscribe to /topic/lobby/all: not connected")
        }
    }
}
