package com.example.securelocker.ui.sharedpref

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.securelocker.data.AppPreference
import com.example.securelocker.data.UserRepository
import com.example.securelocker.util.EncryptionHelper

class SharedPrefViewModel(application: Application) : AndroidViewModel(application) {

    /*private var sharedPreferences = application.getSharedPreferences(
        "com.example.securelocker.pref", Context.MODE_PRIVATE)

     */

    private var sharedPreferences = EncryptionHelper.getSharedPref(application)
    private var appPreference = AppPreference(sharedPreferences)
    private var userRepository = UserRepository(appPreference)

    val userNameField: MutableLiveData<String> = MutableLiveData()
    val userEmailField: MutableLiveData<String> = MutableLiveData()
    val snackBarMessage: MutableLiveData<String> = MutableLiveData()

    fun saveUserData(){
        userRepository.saveUserData(userNameField.value!!,userEmailField.value!!)
        getUserName()
        getUerEmail()
        snackBarMessage.value = "User data saved successfully"
    }

    fun getUserName(){
        userNameField.value =  userRepository.getUserName()
    }

    fun getUerEmail(){
        userEmailField.value = userRepository.getEmail()
    }

}