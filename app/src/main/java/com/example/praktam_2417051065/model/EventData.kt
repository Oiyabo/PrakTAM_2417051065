package com.example.praktam_2417051065.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

/**
 * Domain Model for Event Data used in UI.
 */
data class EventData(
    val nama: String,
    val deskripsi: String,
    val tanggal: LocalDate,
    val image: Any?
)

/**
 * Network Model for Event Data.
 */
data class NetworkEventData(
    @SerializedName("nama") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("tanggal") val tanggal: String,
    @SerializedName("image") val image: String
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): EventData = EventData(
        nama = nama,
        deskripsi = deskripsi,
        tanggal = try {
            LocalDate.parse(tanggal)
        } catch (e: Exception) {
            LocalDate.now()
        },
        image = image
    )
}
