package com.example.co_opapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.co_opapp.Service.PlayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CharacterViewModel(
    private val playerRepository: PlayerRepository,
    private val playerId: String
) : ViewModel() {

    // Compose state, not StateFlow
    private val _characterImageUri = mutableStateOf<Uri?>(null)
    val characterImageUri: State<Uri?> get() = _characterImageUri

    fun setCharacterImage(uri: Uri?) {
        _characterImageUri.value = uri
    }

    fun uploadCharacterImage(context: Context, uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "temp_profile.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }

                val success = playerRepository.uploadProfilePicture(playerId, file)

                withContext(Dispatchers.Main) {
                    if (success) {
                        _characterImageUri.value = uri
                    }
                    onResult(success)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }
}
