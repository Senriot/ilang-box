package com.senriot.ilangbox.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.AdapterView
import android.widget.ListView
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import com.dunst.check.CheckableButton
import com.dunst.check.CheckableImageButton
import com.facebook.drawee.view.SimpleDraweeView
import com.senriot.ilangbox.ui.weiget.ScrollPageView

object DataBindingAdapter {
    @BindingAdapter("onItemClickListener")
    @JvmStatic
    fun itemClickListener(view: ListView, listener: AdapterView.OnItemClickListener) {
        view.onItemClickListener = listener
    }

    @BindingAdapter("uri")
    @JvmStatic
    fun setImageUri(
        view: SimpleDraweeView,
        uri: String?
    ) {
        if (!uri.isNullOrBlank())
            view.setImageURI(uri)

    }

    @SuppressLint("SdCardPath")
    @BindingAdapter("artistUri")
    @JvmStatic
    fun artistImageUri(
        view: SimpleDraweeView,
        uri: String?
    ) {

        if (!uri.isNullOrBlank()) {
            val newfile = uri.replace(".jpg", ".webp")
            view.setImageURI("file:///sdcard/ilang-box/images/artist/$newfile")
        }
    }

    @BindingAdapter("uri")
    @JvmStatic
    fun setImageUri(
        view: SimpleDraweeView,
        uri: Uri?
    ) {
        if (uri != null) {
            view.setImageURI(uri)
        }
    }

    @BindingAdapter("onCheckedChangeListener")
    @JvmStatic
    fun setOnCheckedChangeListener(
        view: CheckableImageButton,
        listener: CheckableImageButton.OnCheckedChangeListener?
    ) {
        listener?.let { view.setOnCheckedChangeListener(it) }
    }

    @BindingAdapter("onChecked")
    @JvmStatic
    fun setChecked(view: CheckableImageButton, checked: Boolean) {
        view.isChecked = checked
    }

    @BindingAdapter("onCheckedChange")
    @JvmStatic
    fun checkChanged(view: CheckableImageButton, checked: (Boolean) -> Unit) {
        view.setOnCheckedChangeListener { button, isChecked ->
            checked(isChecked)
        }
    }

    @BindingAdapter("onChecked")
    @JvmStatic
    fun setChecked(view: CheckableButton, checked: Boolean) {
        view.isChecked = checked
    }

    @BindingAdapter("videoPath")
    @JvmStatic
    fun setVideoPath(view: VideoView, path: String?) {
        path?.let {
            view.setVideoPath(path)
        }
    }

    @BindingAdapter("itemClick")
    @JvmStatic
    fun setItemClick(list: ListView, callback: (position: Int) -> Unit?) {

        list.setOnItemClickListener { _, _, position, id ->
            callback(position)
        }
    }

    @BindingAdapter("surfaceHolderCallback")
    @JvmStatic
    fun surfaceHolderCallback(
        view: SurfaceView,
        callback: (holder: SurfaceHolder?, isCreated: Boolean) -> Unit?
    ) {
        view.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                callback(holder, false)
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                callback(holder, true)
            }
        })
    }

    @BindingAdapter("pageCount", "pageIndex")
    @JvmStatic
    fun setPageCount(view: ScrollPageView, pageCount: Int, pageIndex: Int)
    {
        view.pageCount = pageCount
        view.pageIndex = pageIndex
    }
}
