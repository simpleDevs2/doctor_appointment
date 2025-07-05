package com.example.doctorappoint.common.Helper

import android.util.Base64
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMacUtil {
    const val HMACSHA256 = "HmacSHA256"

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun HMacHexStringEncode(algorithm: String, key: String, data: String): String? {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), algorithm)
        val mac = Mac.getInstance(algorithm)
        mac.init(secretKeySpec)
        val hash = mac.doFinal(data.toByteArray())
        return bytesToHex(hash)
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789abcdef".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }
} 