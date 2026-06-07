package com.example.praktam_2417051065.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.gson.annotations.SerializedName
import androidx.core.graphics.toColorInt

/**
 * Domain Model for Event Cluster used in UI.
 */
data class EventCluster(
    val namaCluster: String,
    val deskripsiCluster: String,
    val daftarEvent: List<EventData>,
    val color: Color,
    val owner: String? = null
)

/**
 * Root object for JSON response.
 */
data class DataClusterResponse(
    @SerializedName("dataCluster") val dataCluster: List<NetworkEventCluster>?
)

/**
 * Network Model for Event Cluster used for Retrofit/GSON.
 */
data class NetworkEventCluster(
    @SerializedName("namaCluster") val namaCluster: String?,
    @SerializedName("deskripsiCluster") val deskripsiCluster: String?,
    @SerializedName("daftarEvent") val daftarEvent: List<NetworkEventData>?,
    @SerializedName("color") val colorString: String?,
    @SerializedName("owner") val owner: String?
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toDomain(): EventCluster = EventCluster(
        namaCluster = namaCluster ?: "Cluster Tanpa Nama",
        deskripsiCluster = deskripsiCluster ?: "",
        daftarEvent = daftarEvent?.map { it.toDomain() } ?: emptyList(),
        color = try {
            Color(colorString?.toColorInt() ?: Color.Gray.toArgb())
        } catch (_: Exception) {
            Color.Gray
        },
        owner = owner
    )
}
