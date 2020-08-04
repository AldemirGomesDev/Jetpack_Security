package com.example.securelocker.ui.file

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.securelocker.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_file.*

class FileFragment : Fragment() {

    companion object {
        fun newInstance() = FileFragment()
    }

    private lateinit var viewModel: FileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(FileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_file, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //observe live data
        observe()

        //fab click
        fab_save_msg.setOnClickListener {
            viewModel.messageBody.value = txtBody.text.toString()
            //viewModel.storeFile()
            viewModel.storeEncryptedFile()
        }
    }

    private fun observe(){
        viewModel.snackBarMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(requireView(),it, Snackbar.LENGTH_SHORT).show()
        })

        viewModel.messageBody.observe(viewLifecycleOwner, Observer {

        })
    }

}