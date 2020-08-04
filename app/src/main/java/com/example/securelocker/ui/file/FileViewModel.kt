package com.example.securelocker.ui.file

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securelocker.util.EncryptionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    val snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val messageBody:MutableLiveData<String> = MutableLiveData()
    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dir = File(context.filesDir,"documents")

    fun storeFile(){
        viewModelScope.launch {
            val now  = Date()
            val fileName = "${sdf.format(now)}.txt"

            if (!dir.exists()){
                dir.mkdir()
            }
            val file = File(dir,fileName)

            launch(Dispatchers.IO) {
                try {
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(messageBody.value!!.toByteArray())

                    fileOutputStream.flush()
                    fileOutputStream.close()
                    snackBarMessage.postValue("Write file was successful")

                }
                catch (e: Exception){
                    snackBarMessage.postValue(e.message)
                }
            }
        }
    }

    fun storeEncryptedFile(){
        viewModelScope.launch {
            val now  = Date()
            val fileName = "${sdf.format(now)}.txt"

            if (!dir.exists()){
                dir.mkdir()
            }
            val file = File(dir,fileName)

            launch(Dispatchers.IO) {
                try {
                    val encryptedFile = EncryptionHelper.getEncryptedFile(file,context)

                    encryptedFile.openFileOutput().also {out->
                        out.write(messageBody.value!!.toByteArray())
                        out.flush()
                        out.close()

                    }
                    snackBarMessage.postValue("Write file was successful")

                }
                catch (e: Exception){
                    snackBarMessage.postValue(e.message)
                }
            }
        }
    }


}