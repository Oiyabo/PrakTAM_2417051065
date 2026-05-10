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
//import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.Repository
import com.example.praktam_2417051065.data.model.DataSource
import com.example.praktam_2417051065.data.model.EventCluster
import com.example.praktam_2417051065.data.model.EventData
import com.example.praktam_2417051065.data.api.RetrofitClient
import com.example.praktam_2417051065.ui.components.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaftarEventScreen(navCon: NavController, repo: Repository) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var retryTrigger by remember { mutableIntStateOf(0) }
    val tag = "DaftarEventScreen"

    LaunchedEffect(retryTrigger) {
        if (repo.currentCluster.isNotEmpty()) {
            isLoading = false; return@LaunchedEffect
        }
        isLoading = true
        isError = runCatching {
            RetrofitClient.instance.getDataCluster().dataCluster?.forEach {
                repo.saveCluster(it.toDomain())
            } ?: error("Empty data")
        }.onFailure { Log.e(tag, "Fetch error: ${it.message}") }.isFailure
        isLoading = false
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

    if (isError) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(android.R.drawable.stat_notify_error),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text("Gagal memuat data", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { retryTrigger++ }) {
                    Text("Coba Lagi")
                }
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

    val displayEvents = remember(selectedCluster, selectedDate, focusOnDate, repo.currentCluster.size) {
        val base = selectedCluster?.daftarEvent ?: repo.currentCluster.flatMap { it.daftarEvent }
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
            CollapsedCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, repo)
        } else {
            MonthlyCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, repo, Modifier.padding(bottom = 8.dp))
        }

        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp).height(40.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            ClusterSelector(selectedCluster, expanded, { expanded = it }, { selectedCluster = it }, repo, Modifier.weight(1f))
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
        ShowDetailedEventInfo(event, placeholderImage) { selectedEvent.value = null }
    }
}
