package com.example.doctorappoint.data.services

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

class OtpService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "OtpService"

    fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (String) -> Unit,
        onVerificationFailed: (FirebaseException) -> Unit
    ) {
        Log.d(TAG, "Sending OTP to: $phoneNumber")
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "Auto-verification completed")
                // Auto-verification completed (SMS received automatically)
                // This usually happens on devices with Google Play Services
                // We don't need to do anything here as user will manually enter OTP
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e(TAG, "Verification failed: ${e.message}")
                onVerificationFailed(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "Code sent successfully, verificationId: $verificationId")
                onCodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(verificationId: String, otpCode: String): Flow<Boolean> = callbackFlow {
        Log.d(TAG, "Verifying OTP: $otpCode")
        
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
            
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "OTP verification successful")
                        trySend(true)
                    } else {
                        Log.e(TAG, "OTP verification failed: ${task.exception?.message}")
                        trySend(false)
                    }
                    close()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "OTP verification failed with exception: ${exception.message}")
                    trySend(false)
                    close(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during OTP verification: ${e.message}")
            trySend(false)
            close(e)
        }
        
        awaitClose()
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        Log.d(TAG, "Signing out user")
        auth.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
} 