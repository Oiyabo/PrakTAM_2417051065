package com.example.praktam_2417051065

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.praktam_2417051065.ui.theme.PrakTAM_2417051065Theme
import com.example.praktam_2417051065.ui.theme.ThemeMode
import model.*
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051065Theme(themeMode = ThemeMode.DARK) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DaftarEventScreen()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaftarEventScreen() {
    var screenType by remember { mutableIntStateOf(0) }
    var focusOnDate by remember { mutableIntStateOf(2) }
    var calCollapse by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedEvent by remember { mutableStateOf<FirstScr?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCluster by remember { mutableStateOf<EventCluster?>(null) }

    val displayEvents = remember(selectedCluster, selectedDate, focusOnDate) {
        val base = selectedCluster?.daftarEvent ?: ScndScr.dataCluster.flatMap { it.daftarEvent }
        base.filter { e ->
            when (focusOnDate) {
                0 -> e.tanggal == selectedDate
                1 -> e.tanggal.month == selectedDate.month && e.tanggal.year == selectedDate.year
                2 -> e.tanggal.year == selectedDate.year
                else -> false
            }
        }
    }

    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(8.dp)) {
        IconButton(onClick = { calCollapse = !calCollapse }, Modifier.height(40.dp)) {
            Icon(
                painterResource(android.R.drawable.ic_menu_my_calendar),
                contentDescription = "Toggle",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        if (calCollapse) CollapsedCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it })
        else MonthlyCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, Modifier.padding(bottom = 8.dp))

        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp).height(40.dp), Arrangement.spacedBy(8.dp), Alignment.CenterVertically) {
            ClusterSelector(selectedCluster, expanded, { expanded = it }, { selectedCluster = it }, Modifier.weight(1f))
            ViewTypeSelector(screenType) { screenType = it }
        }
        Box(Modifier.weight(1f).fillMaxSize()) {
            if (displayEvents.isEmpty()) {
                Text(
                    "Kosong",
                    Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else when (screenType) {
                0 -> DetailScreenBig(displayEvents) { selectedEvent = it }
                1 -> DetailScreenMed(displayEvents) { selectedEvent = it }
                2 -> DetailScreenSmall(displayEvents) { selectedEvent = it }
            }
        }
    }
    selectedEvent?.let { ShowDetailedEventInfo(it) { selectedEvent = null } }
}

@Composable
fun DetailScreenBig(events: List<FirstScr>, onClick: (FirstScr) -> Unit) = LazyColumn(
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    items(events) { e ->
        Card(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(e.imageRes), null, Modifier.size(180.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Spacer(Modifier.height(12.dp))
                Text(e.nama, style = MaterialTheme.typography.titleLarge)
                Text(e.deskripsi, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Tgl: ${e.tanggal}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
                Button(
                    onClick = { onClick(e) },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Detail", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
fun DetailScreenMed(events: List<FirstScr>, onClick: (FirstScr) -> Unit) = LazyColumn(
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    items(events) { e ->
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .clickable { onClick(e) }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painterResource(e.imageRes), null, Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(e.nama, style = MaterialTheme.typography.titleMedium)
                Text(e.deskripsi, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
                Text("Tgl: ${e.tanggal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CollapsedCalendar(selDate: LocalDate, onDate: (LocalDate) -> Unit, onFocus: (Int) -> Unit) {
    var cur by remember { mutableStateOf(selDate.withDayOfMonth(1)) }

    Column(Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onFocus(1); onDate(cur)}) {
            Text("<", Modifier.clickable { cur = cur.minusMonths(1) }.padding(8.dp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text("${cur.month.name} ${cur.year}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(">", Modifier.clickable { cur = cur.plusMonths(1) }.padding(8.dp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(cur.lengthOfMonth()) { i ->
                val date = cur.withDayOfMonth(i + 1)
                val isSel = date == selDate
                val eventCluster = ScndScr.dataCluster.find { cluster ->
                    cluster.daftarEvent.any { event -> event.tanggal == date }
                }
                val eventHighlightColor = eventCluster?.color?.copy(alpha = 0.2f) ?: Color.Transparent
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.primary else eventHighlightColor)
                        .clickable { onDate(date); onFocus(0) },
                    Alignment.Center
                ) {
                    Text(
                        text = (i + 1).toString(),
                        color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = if (isSel) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Preview() = PrakTAM_2417051065Theme(themeMode = ThemeMode.GRAY) { DaftarEventScreen() }
