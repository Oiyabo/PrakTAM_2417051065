package com.example.praktam_2417051065

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.praktam_2417051065.model.EventCluster

class MainViewModel : ViewModel() {
    private val _currentCluster = mutableStateListOf<EventCluster>()
    val currentCluster: List<EventCluster> get() = _currentCluster

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCluster(newCluster: EventCluster) {
        val existingIndex = _currentCluster.indexOfFirst { it.namaCluster == newCluster.namaCluster }
        if (existingIndex != -1) {
            _currentCluster[existingIndex] = newCluster
        } else {
            _currentCluster.add(newCluster)
        }
    }
}
