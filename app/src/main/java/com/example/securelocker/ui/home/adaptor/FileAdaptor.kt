package com.example.securelocker.ui.home.adaptor

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securelocker.R
import com.example.securelocker.data.FileEntity
import kotlinx.android.synthetic.main.secure_file_list_view.view.*

class FileAdaptor(private val mList: ArrayList<FileEntity>,private val onClickListener:(Int) ->Unit) :
    RecyclerView.Adapter<FileAdaptor.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.secure_file_list_view, parent, false)

        return FileViewHolder(view,onClickListener)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileEntity = mList[position]
        holder.onBind(fileEntity)
    }

    class FileViewHolder(itemView: View, val onClickListener: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        lateinit var data: FileEntity

        fun onBind(fileEntity: FileEntity) {
            data = fileEntity

            itemView.txtFileName.text = data.fileName
            itemView.txtFile.text = data.file.toString()
            itemView.txtFileSize.text = data.fileSize

            itemView.setOnClickListener {
                onClickListener(adapterPosition)
            }


        }
    }
}