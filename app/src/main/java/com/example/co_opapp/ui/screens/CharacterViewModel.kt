package com.example.co_opapp.ui.screens

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CharacterViewModel : ViewModel() {
    // Holds the selected character image URI
    private val _characterImageUri = MutableStateFlow<Uri?>(null)
    val characterImageUri: StateFlow<Uri?> = _characterImageUri

    // Update the image URI
    fun setCharacterImage(uri: Uri?) {
        _characterImageUri.value = uri
    }
}
