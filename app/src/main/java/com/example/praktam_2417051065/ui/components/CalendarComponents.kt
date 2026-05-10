package com.example.praktam_2417051065.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
//import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.Repository
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendar(selected: LocalDate, onDate: (LocalDate) -> Unit, onFocus: (Int) -> Unit, repo: Repository, modifier: Modifier = Modifier) {
    var cur by remember { mutableStateOf(selected.withDayOfMonth(1)) }
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        CalendarControl(cur.year.toString(), { cur = cur.minusYears(1) }, { cur = cur.plusYears(1) }, { onFocus(2); onDate(cur) })
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(2.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        ) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach {
                    Text(
                        text = it,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
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
                            val clusterColor = repo.currentCluster.find { it.daftarEvent.any { e -> e.tanggal == d } }?.color?.copy(0.1f) ?: Color.Transparent
                            Box(
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else clusterColor)
                                    .clickable { onDate(d); onFocus(0) },
                                Alignment.Center
                            ) {
                                Text(
                                    text = idx.toString(),
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    style = if (isSel) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall
                                )
                            }
                        } else Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
        CalendarControl(
            label = cur.month.name.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                .take(3),
            onUp = { cur = cur.minusMonths(1) },
            onDown = { cur = cur.plusMonths(1) },
            action = { onFocus(1); onDate(cur) }
        )
    }
}

@Composable
fun CalendarControl(label: String, onUp: () -> Unit, onDown: () -> Unit, action: () -> Unit) = Column(
    Modifier
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
        .fillMaxHeight()
        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        .clickable { action() },
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceAround
) {
    Text("︿", Modifier.clickable { onUp() }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 4.dp), textAlign = TextAlign.Center)
    Text("﹀", Modifier.clickable { onDown() }, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CollapsedCalendar(selDate: LocalDate, onDate: (LocalDate) -> Unit, onFocus: (Int) -> Unit, repo: Repository) {
    var cur by remember { mutableStateOf(selDate.withDayOfMonth(1)) }

    Column(Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("<", Modifier.clickable { cur = cur.minusMonths(1) }.padding(8.dp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text(
                "${cur.month.name} ${cur.year}",
                modifier = Modifier.weight(1f).clickable { onFocus(1); onDate(cur) },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(">", Modifier.clickable { cur = cur.plusMonths(1) }.padding(8.dp), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
            items(cur.lengthOfMonth()) { i ->
                val date = cur.withDayOfMonth(i + 1)
                val isSel = date == selDate
                val eventCluster = repo.currentCluster.find { cluster ->
                    cluster.daftarEvent.any { event -> event.tanggal == date }
                }
                val highlightColor = eventCluster?.color?.copy(alpha = 0.2f) ?: Color.Transparent
                
                Box(
                    Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.primary else highlightColor)
                        .border(1.dp, if (isSel) Color.Transparent else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
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
