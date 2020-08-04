package com.example.securelocker.ui.home

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.example.securelocker.data.AppPreference
import com.example.securelocker.data.FileEntity
import com.example.securelocker.data.UserRepository
import com.example.securelocker.util.EncryptionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.File

class HomeViewModel(application: Application): AndroidViewModel(application) {

    private val context = application
    val masterKey : MutableLiveData<String> = MutableLiveData()
    val newMasterKey : MutableLiveData<String> = MutableLiveData()
    val bitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val message : MutableLiveData<String> = MutableLiveData()
    val fileName: MutableLiveData<String> = MutableLiveData()
    val snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val fileListEntity: MutableLiveData<ArrayList<FileEntity>> = MutableLiveData()
    val fileList = ArrayList<FileEntity>()
    val isProgress : MutableLiveData<Boolean> = MutableLiveData()
    private val dirImage = File(context.filesDir,"images")
    private val dirFile = File(context.filesDir,"documents")
    //
    private var sharedPreferences = EncryptionHelper.getSharedPref(application)
    private var appPreference = AppPreference(sharedPreferences)
    private var userRepository = UserRepository(appPreference)


    fun getFileList(){
        isProgress.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val dir = File(context.filesDir.path)
            val listFiles = dir.listFiles()
            listFiles?.forEach {files->
                if (files.isDirectory){
                    fileList.addAll(files.listFiles()?.map {
                        FileEntity(it.name,it,"${it.length() / 1024} KB")
                    }?.sortedByDescending { it.fileName } ?: emptyList())

                    fileListEntity.postValue(fileList)
                }
                if (files.isFile){
                    fileList.add(FileEntity(files.name,files,"${files.length() / 1024} KB"))
                    fileListEntity.postValue(fileList)
                }

            }
            isProgress.postValue(false)
        }
    }

    fun getEncryptedBitmap(){

        viewModelScope.launch {
            val file  = File(dirImage,fileName.value!!)
            val encryptedFile = EncryptionHelper.getEncryptedFile(file,context)

            launch(Dispatchers.IO) {
                try {
                    encryptedFile.openFileInput().also { input->
                        val byteArrayInputString = ByteArrayInputStream(input.readBytes())
                        bitmap.postValue(BitmapFactory.decodeStream(byteArrayInputString))
                    }
                    snackBarMessage.postValue("Text decrypted successfully")

                }
                catch (e:Exception){
                    snackBarMessage.postValue(e.message)
                }
            }
        }

    }

    fun getEncryptedFile(){

        viewModelScope.launch {
            val file  = File(dirFile,fileName.value!!)
            val encryptedFile = EncryptionHelper.getEncryptedFile(file,context)

            launch(Dispatchers.IO) {
                try {
                    encryptedFile.openFileInput().also { input->
                       message.postValue(String(input.readBytes(),Charsets.UTF_8))
                    }
                    snackBarMessage.postValue("Image decrypted successfully")

                }
                catch (e:Exception){
                    snackBarMessage.postValue(e.message)
                }
            }
        }

    }

    fun getMaster(){
        masterKey.value = userRepository.getMasterKey()
    }

    fun setMasterKey(){
        userRepository.setMasterKey(masterKey.value!!)
        snackBarMessage.value = "Master key setup successfully"
    }

    fun updateMasterKey(){
        if (masterKey.value != userRepository.getMasterKey()){
            snackBarMessage.value = "Master key did not match"
        }
        else {
            userRepository.setMasterKey(newMasterKey.value!!)
            snackBarMessage.value = "Master key update successfully"
        }

    }





}