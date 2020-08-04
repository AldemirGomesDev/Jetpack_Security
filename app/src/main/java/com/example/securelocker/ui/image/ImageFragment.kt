package com.example.securelocker.ui.image

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.securelocker.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_image.*

class ImageFragment : Fragment() {
    companion object {
        const val RC_CAPTURE_IMAGE = 100
        const val RC_PERMISSION = 200
    }

    private lateinit var viewModel: ImageViewModel
    val permissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        return  inflater.inflate(R.layout.fragment_image, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe live data
        observer()

        fb_capture_image.setOnClickListener {
            if (allPermissionGranted()){
                startCamera()
            }
            else {
                ActivityCompat.requestPermissions(requireActivity(),permissions, RC_PERMISSION)
            }
        }
    }

    private fun observer(){
        viewModel.snackBarMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(),it, Snackbar.LENGTH_SHORT).show()

        })

        viewModel.bitmap.observe(viewLifecycleOwner, Observer {
            imageView.setImageBitmap(it)
        })
    }

    private fun startCamera(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent->
            intent.resolveActivity(requireContext().packageManager)?.also {
                startActivityForResult(intent, RC_CAPTURE_IMAGE)
            }
        }
    }

    private fun allPermissionGranted() = permissions.all {
        ContextCompat.checkSelfPermission(
            requireContext(),it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RC_PERMISSION){
            if (allPermissionGranted()){
                startCamera()
            }
            else {
                viewModel.snackBarMessage.value = "Permissions not granted by the user"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_CAPTURE_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                val bitmap = data?.extras?.get("data") as Bitmap
                viewModel.bitmap.value = bitmap
                viewModel.storeEncryptedBitmap()
            }
        }
    }
}