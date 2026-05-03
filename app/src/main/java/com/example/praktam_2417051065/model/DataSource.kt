package com.example.praktam_2417051065.model

import android.content.Context
import com.example.praktam_2417051065.R

object DataSource {
    fun getResourceId(context: Context, imageName: String?): Int {
        if (imageName.isNullOrBlank()) return R.drawable.noimg

        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        return if (resourceId != 0) resourceId else R.drawable.noimg
    }
}
