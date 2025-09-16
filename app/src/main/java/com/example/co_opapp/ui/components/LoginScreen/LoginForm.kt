package com.example.co_opapp.ui.components.LoginScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// --- A hook-like state holder for login form ---
@Composable
fun rememberLoginFormState(): LoginFormState {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }

    return remember {
        LoginFormState(
            usernameState = { username },
            onUsernameChange = { username = it },
            passwordState = { password },
            onPasswordChange = { password = it },
            message = message
        )
    }
}

// --- A simple data holder ---
class LoginFormState(
    val usernameState: () -> String,
    val onUsernameChange: (String) -> Unit,
    val passwordState: () -> String,
    val onPasswordChange: (String) -> Unit,
    val message: MutableState<String>
) {
    val username get() = usernameState()
    val password get() = passwordState()
}
