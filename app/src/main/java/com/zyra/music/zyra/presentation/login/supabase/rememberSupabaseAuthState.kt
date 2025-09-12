package com.zyra.music.zyra.presentation.login.supabase


import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.zyra.music.zyra.R
import com.zyra.music.zyra.data.remote.SupabaseClient
import com.zyra.music.zyra.presentation.login.AuthState
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.buildJsonObject
import java.security.MessageDigest
import java.util.UUID

private val TAG = "AuthState"
@Composable
fun rememberSupabaseAuthState(
    onSignInSuccess: () -> Unit,
    onSignInFailed: (Exception) -> Unit,
    onLoadingChange: (Boolean) -> Unit
): AuthState {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()


    val googleSignClient = remember {
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
    }

    return remember {
        AuthState(
            onGoogleSignIn = {
                onLoadingChange(true)
                scope.launch {
                   try {
                       Log.d(TAG, "Starting Google Sign-In flow...")
                       val credentialManager = CredentialManager.create(context)

                       // 1) Nonce: raw -> hashed
                       val rawNonce = UUID.randomUUID().toString()
                       val hashedNonce = MessageDigest.getInstance("SHA-256")
                           .digest(rawNonce.toByteArray())
                           .joinToString("") { "%02x".format(it) }

                       val googleIdOption = GetGoogleIdOption.Builder()
                           .setFilterByAuthorizedAccounts(false)
                           .setServerClientId(context.getString(R.string.google_sign_in_client_id))
                           .setNonce(hashedNonce)
                           .build()

                       val request = GetCredentialRequest.Builder()
                           .addCredentialOption(googleIdOption)
                           .build()
                       Log.d(TAG, "Requesting credential from CredentialManager...")
                       val result = credentialManager.getCredential(context,request)
                       val credential = result.credential
                       Log.d(TAG, "Credential received successfully.")

                       val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                       val googleIdToken = googleIdTokenCredential.idToken
                       Log.d(TAG, "Extracted ID Token:")

                       Log.d(TAG, "Attempting to sign in to Supabase with token...")


                       Log.d(TAG, "Attempting to sign in to Supabase with token...")

                       SupabaseClient.supabase.auth.signInWith(IDToken){
                           idToken = googleIdToken
                           provider = Google
                           nonce = rawNonce
                       }

                       Log.d(TAG, "Supabase sign-in successful.")

                       onLoadingChange(false)
                       onSignInSuccess()
                   } catch (e : Exception){
                       Log.e(TAG, "Sign-in failed at some point", e) // Log the full exception
                       onLoadingChange(false)
                       onSignInFailed(e)
                       Toast.makeText(context, "Sign-in failed : ${e.message}", Toast.LENGTH_SHORT).show()
                   }

                }
            },
            onEmailSignIn = { email, password ->
                onLoadingChange(true)
                scope.launch {
                    try {
                        SupabaseClient.supabase.auth.signInWith(Email) {
                            this.email = email
                            this.password = password
                        }
                        onLoadingChange(false)
                        onSignInSuccess()
                    } catch (e: Exception) {
                        onLoadingChange(false)
                        onSignInFailed(e)
                    }
                }
            },
            onSignOut = {
                scope.launch {
                    onLoadingChange(true)
                    googleSignClient.signOut().await()
                    SupabaseClient.supabase.auth.signOut()
                    onLoadingChange(false)
                }
            },
            getProfileImage = {
                SupabaseClient.supabase.auth.currentUserOrNull()?.userMetadata?.get("avatar_url") as? String
            }
        )
    }

}