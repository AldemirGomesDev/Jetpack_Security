package com.example.securelocker.data

class UserRepository (private val appPreference: AppPreference){


    fun saveUserData(name: String, email: String){
        appPreference.setUserName(name)
        appPreference.setEmail(email)

    }

    fun getUserName(): String? = appPreference.getUserName()

    fun getEmail(): String? = appPreference.getEmail()

    fun setMasterKey(key: String){
        appPreference.setMasterKey(key)
    }

    fun getMasterKey(): String? = appPreference.getMasterKey()

}