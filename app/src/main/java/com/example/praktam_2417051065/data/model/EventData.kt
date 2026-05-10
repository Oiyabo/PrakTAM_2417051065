package com.example.praktam_2417051065.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

// Domain Model for Event Data used in UI.
data class EventData(
    val nama: String,
    val deskripsi: String,
    val tanggal: LocalDate,
    val imageUrl: String
)

// Network Model for Event Data.
data class NetworkEventData(
    @SerializedName("nama") val nama: String?,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("tanggal") val tanggal: String?,
    @SerializedName("image") val imageUrl: String?
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): EventData = EventData(
        nama = nama ?: "Tanpa Nama",
        deskripsi = deskripsi ?: "",
        tanggal = try {
            LocalDate.parse(tanggal)
        } catch (_: Exception) {
            LocalDate.now()
        },
        imageUrl = imageUrl ?: ""
    )
}
