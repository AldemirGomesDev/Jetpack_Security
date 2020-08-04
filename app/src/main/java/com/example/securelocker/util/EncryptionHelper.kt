package com.example.securelocker.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.io.File

object EncryptionHelper {

    private val prefName = "com.example.securelocker.pref"
    private val masterKeyAlies = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private val MASTER_KEY_ALIAS ="master_key_secure_data"


    fun getSharedPref(context: Context): SharedPreferences {
        val keyEncryptedScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV
        val valueEncryptedScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

        return EncryptedSharedPreferences.create(
            prefName,
            masterKeyAlies,
            context,
            keyEncryptedScheme,
            valueEncryptedScheme
        )
    }

    fun getEncryptedFile(file: File, context: Context): EncryptedFile {
        val fileEncryptionScheme = EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB

        return EncryptedFile.Builder(
            file,
            context,
            masterKeyAlies,
            fileEncryptionScheme
        ).build()

    }

    fun getAdvanceEncryptedFile(file: File, context: Context, isBiometric: Boolean? = false): EncryptedFile {
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            MASTER_KEY_ALIAS,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setKeySize(256)

            isBiometric?.let {
                if (it) {
                    setUserAuthenticationRequired(true)
                    setUserAuthenticationValidityDurationSeconds(15)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                setUnlockedDeviceRequired(true)
                setIsStrongBoxBacked(true)
            }


        }.build()

        val masterKeyAlies = MasterKeys.getOrCreate(keyGenParameterSpec)
        val fileEncryptionScheme = EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB

        return EncryptedFile.Builder(
            file,
            context,
            masterKeyAlies,
            fileEncryptionScheme
        ).build()

    }


}