package com.zyra.music.zyra.presentation.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zyra.music.zyra.R
import com.zyra.music.zyra.presentation.login.component.CustomTextBox
import com.zyra.music.zyra.presentation.login.supabase.rememberSupabaseAuthState

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess : () -> Unit


) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var resetEmail by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isResetEmailVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val authState = rememberSupabaseAuthState(
        onLoadingChange = { loading ->
            isLoading = loading
        },
        onSignInSuccess = {
            onLoginSuccess()
            Toast.makeText(context, "Sign In Successful!", Toast.LENGTH_LONG).show()
        },
        onSignInFailed = { error ->
            Toast.makeText(context, "Sign In Failed : ${error.message}", Toast.LENGTH_LONG).show()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.drawable.login_bgm),
                contentScale = ContentScale.Crop
            )
            .padding(horizontal = 24.dp)

    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CustomTextBox(
                value = email,
                label = "Email",
                placeholder = "Enter your email",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onValueChange = {
                    email = it
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.envelope),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            )
            Spacer(Modifier.height(12.dp))
            CustomTextBox(
                value = password,
                label = "Password",
                placeholder = "Enter your password",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
//                supportingText = "Write a password which contains uppercase, digit, and symbol",
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                onValueChange = {
                    password = it
                },
                trailingIcon = {
                    IconButton(onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }) {
                        Icon(
                            painter = if (isPasswordVisible) painterResource(R.drawable.eye_crossed) else painterResource(
                                R.drawable.eye
                            ),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            )
            Button(onClick = {
                authState.onEmailSignIn(email, password)
            },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
                Text(text = "Login")
            }
            Spacer(Modifier.height(12.dp))
            Text(text = "Forgot Password?", color = Color.White)
            Spacer(Modifier.height(16.dp))
            Text(text = "New User? Register", color = Color.White)
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    error = null
                    authState.onGoogleSignIn()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)){
                    Image(modifier= Modifier.size(20.dp),painter = painterResource(id = R.drawable.google_logo), contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Sign In with Google", color = Color.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLogin() {

}