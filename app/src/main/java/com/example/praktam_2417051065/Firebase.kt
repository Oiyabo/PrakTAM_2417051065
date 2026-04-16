package com.example.praktam_2417051065

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import model.EventCluster
import model.FirstScr
import java.time.LocalDate

object FirebaseRepository {
    private val db = Firebase.firestore
    private val clusterCollection = db.collection("eventClusters")

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllClusters(): List<EventCluster> {
        return try {
            val snapshot = clusterCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val namaCluster = doc.getString("namaCluster") ?: ""
                val deskripsiCluster = doc.getString("deskripsiCluster") ?: ""
                val colorLong = doc.getLong("color") ?: 0L
                val color = Color(colorLong.toULong())

                val eventsData = doc.get("daftarEvent") as? List<Map<String, Any>> ?: emptyList()
                val daftarEvent = eventsData.map { e ->
                    val imageData = e["image"]
                    FirstScr(
                        nama = e["nama"] as? String ?: "",
                        deskripsi = e["deskripsi"] as? String ?: "",
                        tanggal = LocalDate.parse(
                            e["tanggal"] as? String ?: LocalDate.now().toString()
                        ),

                        image = if (imageData is Long) imageData.toInt() else imageData
                    )
                }

                EventCluster(namaCluster, deskripsiCluster, daftarEvent, color)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateCluster(cluster: EventCluster) {
        try {
            val data = mapOf(
                "namaCluster" to cluster.namaCluster,
                "deskripsiCluster" to cluster.deskripsiCluster,
                "color" to cluster.color.value.toLong(),
                "daftarEvent" to cluster.daftarEvent.map { e ->
                    mapOf(
                        "nama" to e.nama,
                        "deskripsi" to e.deskripsi,
                        "tanggal" to e.tanggal.toString(),
                        "image" to when (val img = e.image) {
                            is Int -> img
                            else -> img?.toString() ?: ""
                        }
                    )
                }
            )

            clusterCollection.document(cluster.namaCluster).set(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
