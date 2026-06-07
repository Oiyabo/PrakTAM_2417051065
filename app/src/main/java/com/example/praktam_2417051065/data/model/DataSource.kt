package com.example.praktam_2417051065.data.model

import android.annotation.SuppressLint
import android.content.Context
import com.example.praktam_2417051065.R

object DataSource {
    @SuppressLint("DiscouragedApi")
    fun getResourceId(context: Context, imageName: String?): Int {
        if (imageName.isNullOrBlank()) return android.R.drawable.ic_menu_day

        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        return if (resourceId != 0) resourceId else R.drawable.noimg
    }
}
