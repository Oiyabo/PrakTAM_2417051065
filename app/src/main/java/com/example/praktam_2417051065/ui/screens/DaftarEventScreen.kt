package com.example.praktam_2417051065.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.model.DataSource
import com.example.praktam_2417051065.model.EventCluster
import com.example.praktam_2417051065.model.EventData
import com.example.praktam_2417051065.network.RetrofitClient
import com.example.praktam_2417051065.ui.components.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaftarEventScreen(navCon: NavController, viewModel: MainViewModel) {
    var isLoading by remember { mutableStateOf(true) }
    val tag = "DaftarEventScreen"

    LaunchedEffect(Unit) {
        if (viewModel.currentCluster.isEmpty()) {
            try {
                Log.d(tag, "Memulai fetching data dari API...")
                val response = RetrofitClient.instance.getDataCluster()
                Log.d(tag, "Data berhasil diambil. Jumlah cluster: ${response.dataCluster.size}")
                
                response.dataCluster.forEach { networkCluster ->
                    Log.d(tag, "Mapping cluster: ${networkCluster.namaCluster} dengan ${networkCluster.daftarEvent.size} event")
                    viewModel.saveCluster(networkCluster.toDomain())
                }
            } catch (e: Exception) {
                Log.e(tag, "Terjadi kesalahan saat fetching data: ${e.message}", e)
            } finally {
                isLoading = false
            }
        } else {
            Log.d(tag, "Data sudah ada di ViewModel, melewati fetching.")
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Memuat data...")
            }
        }
        return
    }

    var screenType by remember { mutableIntStateOf(0) }
    var focusOnDate by remember { mutableIntStateOf(1) }
    var calCollapse by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(LocalDate.of(2026, 3, 1)) }
    
    val selectedEvent = remember { mutableStateOf<EventData?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCluster by remember { mutableStateOf<EventCluster?>(null) }

    val context = LocalContext.current
    val placeholderImage = DataSource.getResourceId(context, "noimg")

    val displayEvents = remember(selectedCluster, selectedDate, focusOnDate, viewModel.currentCluster.size) {
        val base = selectedCluster?.daftarEvent ?: viewModel.currentCluster.flatMap { it.daftarEvent }
        val filtered = base.filter { e ->
            when (focusOnDate) {
                0 -> e.tanggal == selectedDate
                1 -> e.tanggal.month == selectedDate.month && e.tanggal.year == selectedDate.year
                2 -> e.tanggal.year == selectedDate.year
                else -> false
            }
        }
        filtered
    }

    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            IconButton(onClick = { calCollapse = !calCollapse }, Modifier.height(40.dp)) {
                Icon(
                    painterResource(android.R.drawable.ic_menu_my_calendar),
                    contentDescription = "Toggle Calendar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Button(modifier = Modifier.height(40.dp), onClick = { navCon.navigate("addPage") }) {
                Text("+ Cluster")
            }
        }

        if (calCollapse) {
            CollapsedCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, viewModel)
        } else {
            MonthlyCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, viewModel, Modifier.padding(bottom = 8.dp))
        }

        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp).height(40.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            ClusterSelector(selectedCluster, expanded, { expanded = it }, { selectedCluster = it }, viewModel, Modifier.weight(1f))
            ViewTypeSelector(screenType) { screenType = it }
        }

        Box(Modifier.weight(1f).fillMaxSize()) {
            if (displayEvents.isEmpty()) {
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tidak ada event", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyLarge)
                    Text("Pastikan kalender berada di Maret 2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
            } else {
                when (screenType) {
                    0 -> DetailScreenBig(displayEvents, placeholderImage) { selectedEvent.value = it }
                    1 -> DetailScreenMed(displayEvents, placeholderImage) { selectedEvent.value = it }
                    2 -> DetailScreenSmall(displayEvents, placeholderImage) { selectedEvent.value = it }
                }
            }
        }
    }

    selectedEvent.value?.let { event ->
        ShowDetailedEventInfo(event) { selectedEvent.value = null }
    }
}
