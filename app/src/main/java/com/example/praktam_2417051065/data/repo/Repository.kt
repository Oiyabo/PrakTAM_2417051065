package com.example.praktam_2417051065

import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.praktam_2417051065.data.model.EventCluster
import com.example.praktam_2417051065.data.model.EventData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.UUID

class Repository {
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val TAG = "Repository"

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveClusterToFirestore(newCluster: EventCluster): Boolean {
        val clusterData = mapOf(
            "id" to newCluster.id,
            "namaCluster" to newCluster.namaCluster,
            "deskripsiCluster" to newCluster.deskripsiCluster,
            "color" to newCluster.color.toArgb().toLong(),
            "owner" to newCluster.owner,
            "daftarEvent" to newCluster.daftarEvent.map { event ->
                mapOf(
                    "nama" to event.nama,
                    "deskripsi" to event.deskripsi,
                    "tanggal" to event.tanggal.toString(),
                    "image" to event.imageUrl,
                    "alarmEnabled" to event.alarmEnabled,
                    "alarmTime" to event.alarmTime
                )
            }
        )

        return try {
            firestore.collection("eventClusters").document(newCluster.id)
                .set(clusterData).await()
            Log.d(TAG, "Cluster successfully saved to Firestore: ${newCluster.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving cluster to Firestore", e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchFromFirestore(): List<EventCluster> {
        return try {
            val result = firestore.collection("eventClusters").get().await()
            val clusters = mutableListOf<EventCluster>()
            for (document in result) {
                val id = document.getString("id") ?: document.id
                val namaCluster = document.getString("namaCluster") ?: "Cluster Tanpa Nama"
                val deskripsiCluster = document.getString("deskripsiCluster") ?: ""
                val colorLong = document.getLong("color") ?: 0xFF808080L
                val color = Color(colorLong.toInt())

                @Suppress("UNCHECKED_CAST")
                val daftarEventList = document.get("daftarEvent") as? List<Map<String, Any>> ?: emptyList()

                val events = daftarEventList.map { eventMap ->
                    val nama = eventMap["nama"] as? String ?: ""
                    val deskripsi = eventMap["deskripsi"] as? String ?: ""
                    val tanggalStr = eventMap["tanggal"] as? String ?: ""
                    val tanggal = try {
                        LocalDate.parse(tanggalStr)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                    val imageUrl = eventMap["image"] as? String ?: ""
                    val alarmEnabled = eventMap["alarmEnabled"] as? Boolean ?: false
                    val alarmTime = eventMap["alarmTime"] as? String
                    EventData(nama, deskripsi, tanggal, imageUrl, alarmEnabled, alarmTime)
                }

                val owner = document.getString("owner")

                clusters.add(EventCluster(id, namaCluster, deskripsiCluster, events, color, owner))
            }
            Log.d(TAG, "Successfully fetched ${clusters.size} clusters from Firestore")
            clusters
        } catch (exception: Exception) {
            Log.e(TAG, "Error fetching from Firestore", exception)
            emptyList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getClusterById(id: String): EventCluster? {
        return try {
            val document = firestore.collection("eventClusters").document(id).get().await()
            if (document.exists()) {
                val docId = document.getString("id") ?: document.id
                val namaCluster = document.getString("namaCluster") ?: "Cluster Tanpa Nama"
                val deskripsiCluster = document.getString("deskripsiCluster") ?: ""
                val colorLong = document.getLong("color") ?: 0xFF808080L
                val color = Color(colorLong.toInt())

                @Suppress("UNCHECKED_CAST")
                val daftarEventList = document.get("daftarEvent") as? List<Map<String, Any>> ?: emptyList()

                val events = daftarEventList.map { eventMap ->
                    val nama = eventMap["nama"] as? String ?: ""
                    val deskripsi = eventMap["deskripsi"] as? String ?: ""
                    val tanggalStr = eventMap["tanggal"] as? String ?: ""
                    val tanggal = try {
                        LocalDate.parse(tanggalStr)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                    val imageUrl = eventMap["image"] as? String ?: ""
                    val alarmEnabled = eventMap["alarmEnabled"] as? Boolean ?: false
                    val alarmTime = eventMap["alarmTime"] as? String
                    EventData(nama, deskripsi, tanggal, imageUrl, alarmEnabled, alarmTime)
                }

                val owner = document.getString("owner")

                EventCluster(docId, namaCluster, deskripsiCluster, events, color, owner)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cluster by ID", e)
            null
        }
    }

    suspend fun uploadImageToStorage(uri: Uri, clusterName: String, eventName: String): String? {
        val uniqueID = UUID.randomUUID().toString()
        val ref = storage.reference.child("events/${clusterName}/${eventName}_${uniqueID}.jpg")
        return try {
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed", e)
            null
        }
    }
}
