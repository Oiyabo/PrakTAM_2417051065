package com.example.praktam_2417051065

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import model.*
import java.time.LocalDate

@Composable
fun ShowDetailedEventInfo(e: FirstScr, onDismiss: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = { TextButton(onDismiss) { Text("Tutup") } },
    title = { Text(e.nama, fontWeight = FontWeight.Bold) },
    text = {
        Column {
            Image(painterResource(e.imageRes), null, Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.height(16.dp))
            Text("Tanggal: ${e.tanggal}", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
            Text(e.deskripsi, style = MaterialTheme.typography.bodyLarge)
        }
    }
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendar(selected: LocalDate, onDate: (LocalDate) -> Unit, onFocus: (Int) -> Unit, modifier: Modifier = Modifier) {
    var cur by remember { mutableStateOf(selected.withDayOfMonth(1)) }
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        CalendarControl(cur.year.toString(), { cur = cur.minusYears(1) }, { cur = cur.plusYears(1) }, { onFocus(2); onDate(cur) })
        Column(Modifier.weight(1f).fillMaxHeight().background(Color.White, RoundedCornerShape(8.dp)).padding(2.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { Text(it, Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelMedium, color = Color.Gray, fontWeight = FontWeight.Bold) }
            }
            val offset = cur.withDayOfMonth(1).dayOfWeek.value % 7
            val days = cur.lengthOfMonth()
            (0 until (offset + days + 6) / 7).forEach { row ->
                Row(Modifier.fillMaxWidth()) {
                    (0 until 7).forEach { col ->
                        val idx = row * 7 + col - offset + 1
                        if (idx in 1..days) {
                            val d = cur.withDayOfMonth(idx)
                            val isSel = d == selected
                            val color = ScndScr.dataCluster.find { it.daftarEvent.any { e -> e.tanggal == d } }?.color?.copy(0.1f) ?: Color.Transparent
                            Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp).clip(RoundedCornerShape(8.dp)).background(color).clickable { onDate(d); onFocus(0) }, Alignment.Center) {
                                if (isSel) Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color(0x266200EE)))
                                Text(idx.toString(), color = if (isSel) Color.White else Color.Black, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        } else Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        CalendarControl(cur.month.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            .take(3), { cur = cur.minusMonths(1) }, { cur = cur.plusMonths(1) }, { onFocus(1); onDate(cur) })
    }
}

@Composable
fun CalendarControl(label: String, onUp: () -> Unit, onDown: () -> Unit, action: () -> Unit) = Column(
    Modifier.background(Color.White, RoundedCornerShape(8.dp)).fillMaxHeight().border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)).clickable { action() },
    horizontalAlignment =  Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceAround
) {
    Text("︿", Modifier.clickable { onUp() }, fontWeight = FontWeight.Bold)
    Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp), textAlign = TextAlign.Center)
    Text("﹀", Modifier.clickable { onDown() }, fontWeight = FontWeight.Bold)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClusterSelector(cluster: EventCluster?, exp: Boolean, onExp: (Boolean) -> Unit, onSel: (EventCluster?) -> Unit, modifier: Modifier) = Box(modifier.fillMaxHeight()) {
    Row(Modifier.fillMaxSize().background(Color.Gray, RoundedCornerShape(15.dp)).clickable { onExp(true) }.padding(horizontal = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(cluster?.namaCluster ?: "All Clusters", color = Color.White, fontSize = 14.sp, maxLines = 1)
        Text("﹀", color = Color.White, fontWeight = FontWeight.Bold)
    }
    DropdownMenu(exp, { onExp(false) }, modifier = Modifier.background(Color.White).width(IntrinsicSize.Max)) {
        DropdownMenuItem(text = { Text("All Clusters") }, onClick = { onSel(null); onExp(false) })
        ScndScr.dataCluster.forEach { c ->
            DropdownMenuItem(text = { Column { Text(c.namaCluster, fontWeight = FontWeight.Bold); Text(c.deskripsiCluster, style = MaterialTheme.typography.bodySmall, color = Color.Gray) } }, onClick = { onSel(c); onExp(false) })
        }
    }
}

@Composable
fun ViewTypeSelector(type: Int, onSel: (Int) -> Unit) = Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    repeat(3) { i ->
        Box(Modifier.size(40.dp).border(1.dp, Color.Gray, RoundedCornerShape(8.dp)).background(if (type == i) Color.White else Color.LightGray, RoundedCornerShape(8.dp)).clickable { onSel(i) }, Alignment.Center) { Text((i + 1).toString()) }
    }
}


@Composable
fun DetailScreenSmall(events: List<FirstScr>, onClick: (FirstScr) -> Unit) = LazyVerticalGrid(GridCells.Adaptive(80.dp), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    items(events) { e ->
        Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).clickable { onClick(e) }) {
            Image(painterResource(e.imageRes), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
            Column(Modifier.fillMaxSize().padding(4.dp), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
                Text(e.nama, color = Color.White, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1)
                Text(e.tanggal.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}