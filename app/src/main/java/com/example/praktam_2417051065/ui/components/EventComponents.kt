package com.example.praktam_2417051065.ui.components

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051065.data.model.EventData
import coil.compose.AsyncImage
import java.time.LocalDate

@Composable
fun ShowDetailedEventInfo(e: EventData, placeholder: Int, isOwner: Boolean = false, onEditCluster: () -> Unit = {}, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } },
        dismissButton = {
            if (isOwner) {
                TextButton(onClick = { onDismiss(); onEditCluster() }) { Text("Edit Event") }
            }
        },
        title = { Text(e.nama, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                AsyncImage(
                    model = e.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(placeholder)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Tanggal: ${e.tanggal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = e.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
fun EventListElegant(events: List<EventData>, placeholder: Int, isEventOwned: (EventData) -> Boolean, onClick: (EventData) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { e ->
            val owned = isEventOwned(e)
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .clickable { onClick(e) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = e.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(placeholder)
                )
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(e.nama, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        if (owned) {
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Filled.Edit, contentDescription = "Milik Anda", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Text(e.deskripsi, maxLines = 1, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Tgl: ${e.tanggal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEventPopUp(onAdd: (EventData) -> Unit, mod: EventData? = null, onDismiss: () -> Unit) {
    var nama by remember { mutableStateOf(mod?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(mod?.deskripsi ?: "") }
    var tanggalStr by remember { mutableStateOf(mod?.tanggal?.toString() ?: LocalDate.now().toString()) }
    var img by remember { mutableStateOf<Any?>(mod?.imageUrl ?: android.R.drawable.ic_menu_gallery) }
    var alarmEnabled by remember { mutableStateOf(mod?.alarmEnabled ?: false) }
    var alarmTime by remember { mutableStateOf(mod?.alarmTime ?: "") }
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (mod == null) "Tambah Event Baru" else "Edit Event",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = nama, 
                onValueChange = { nama = it }, 
                label = { Text("Nama Event") }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = deskripsi, 
                onValueChange = { deskripsi = it }, 
                label = { Text("Deskripsi") }, 
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            InputDateChoice(value = tanggalStr, onValueChange = { tanggalStr = it }, label = { Text("Tanggal") })
            UploadImage(value = img, onValueChange = { img = it }, label = { Text("Gambar Event") })
            
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                horizontalArrangement = Arrangement.SpaceBetween, 
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Setel Alarm & Pengingat")
                Switch(checked = alarmEnabled, onCheckedChange = { alarmEnabled = it })
            }
            if (alarmEnabled) {
                InputTimeChoice(
                    value = alarmTime, 
                    onValueChange = { alarmTime = it }, 
                    label = { Text("Waktu Alarm") }
                )
            }
            
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Batal") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    if (nama.isBlank()) {
                        Toast.makeText(context, "Nama Event harus diisi", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val date = try { LocalDate.parse(tanggalStr) } catch (_: Exception) { LocalDate.now() }
                    val imgStr = when (img) {
                        is Uri -> img.toString()
                        is Int -> img.toString()
                        is String -> img as String
                        else -> ""
                    }
                    if (alarmEnabled && alarmTime.isBlank()) {
                        Toast.makeText(context, "Pilih jam untuk alarm", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    onAdd(EventData(nama, deskripsi, date, imgStr, alarmEnabled, if (alarmEnabled) alarmTime else null))
                }) {
                    Text(if (mod == null) "Simpan Event" else "Update Event")
                }
            }
        }
    }
}

@Composable
fun EventRowCard(
    e: EventData,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isHighlighted: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlighted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = e.nama, 
                        fontWeight = FontWeight.Bold, 
                        color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified
                    )
                    if (isHighlighted) {
                        Spacer(Modifier.width(8.dp))
                        SuggestionChip(
                            onClick = {},
                            label = { Text("Event Ini", fontSize = 10.sp) },
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }
                Text(
                    text = e.tanggal.toString(), 
                    style = MaterialTheme.typography.bodySmall, 
                    color = if (isHighlighted) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else Color.Unspecified
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onEdit) {
                    Text("Edit", color = if (isHighlighted) MaterialTheme.colorScheme.primary else Color.Unspecified)
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
