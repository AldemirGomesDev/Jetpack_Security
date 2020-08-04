package com.example.securelocker.ui.image

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.securelocker.util.EncryptionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    val snackBarMessage: MutableLiveData<String> = MutableLiveData()
    val bitmap:MutableLiveData<Bitmap> = MutableLiveData()
    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dir = File(context.filesDir,"images")


    fun storeEncryptedBitmap(){
        viewModelScope.launch {
            val scaleBitmap = Bitmap.createScaledBitmap(
                bitmap.value!!,1080,780,true
            )
            val now  = Date()
            val fileName = "${sdf.format(now)}.jpg"

            if (!dir.exists()){
                dir.mkdir()
            }

            val file  = File(dir, fileName)

            launch(Dispatchers.IO) {
                try {
                    val encryptedFile = EncryptionHelper.getEncryptedFile(file,context)

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    scaleBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)

                    encryptedFile.openFileOutput().also {out->
                        out.write(byteArrayOutputStream.toByteArray())

                        out.flush()
                        out.close()
                    }
                    snackBarMessage.postValue("bitmap encrypted successfully")

                }
                catch (e: Exception){
                    snackBarMessage.postValue(e.message)
                }
            }
        }
    }

}