package com.example.praktam_2417051065

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import model.*
import java.time.LocalDate
import java.util.Calendar
import kotlinx.coroutines.launch
import android.widget.Toast

@Composable
fun ShowDetailedEventInfo(e: FirstScr, onDismiss: () -> Unit) = AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } },
    title = { Text(e.nama, style = MaterialTheme.typography.titleLarge) },
    text = {
        Column {
            AsyncImage(
                model = e.image,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthlyCalendar(selected: LocalDate, onDate: (LocalDate) -> Unit, onFocus: (Int) -> Unit, modifier: Modifier = Modifier) {
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
                            val clusterColor = CurrenCluster.find { it.daftarEvent.any { e -> e.tanggal == d } }?.color?.copy(0.1f) ?: Color.Transparent
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
fun ClusterSelector(cluster: EventCluster?, exp: Boolean, onExp: (Boolean) -> Unit, onSel: (EventCluster?) -> Unit, modifier: Modifier) = Box(modifier.fillMaxHeight()) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(15.dp))
            .clickable { onExp(true) }
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(cluster?.namaCluster ?: "All Clusters", color = MaterialTheme.colorScheme.onSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        Text("﹀", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold)
    }
    DropdownMenu(
        expanded = exp,
        onDismissRequest = { onExp(false) },
        modifier = Modifier.background(MaterialTheme.colorScheme.surface).width(IntrinsicSize.Max)
    ) {
        DropdownMenuItem(
            text = { Text("All Clusters", style = MaterialTheme.typography.bodyMedium) },
            onClick = { onSel(null); onExp(false) }
        )
        CurrenCluster.forEach { c ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(c.namaCluster, style = MaterialTheme.typography.titleSmall)
                        Text(c.deskripsiCluster, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                onClick = { onSel(c); onExp(false) }
            )
        }
    }
}

@Composable
fun ViewTypeSelector(type: Int, onSel: (Int) -> Unit) = Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    repeat(3) { i ->
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                .background(
                    if (type == i) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(8.dp)
                )
                .clickable { onSel(i) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (i + 1).toString(),
                color = if (type == i) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun DetailScreenSmall(events: List<FirstScr>, onClick: (FirstScr) -> Unit) = LazyVerticalGrid(
    columns = GridCells.Adaptive(80.dp),
    contentPadding = PaddingValues(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {
    items(events) { e ->
        Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(8.dp)).clickable { onClick(e) }) {
            AsyncImage(model = e.image, contentDescription = null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
            Column(Modifier.fillMaxSize().padding(4.dp), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
                Text(e.nama, color = Color.White, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1)
                Text(e.tanggal.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddPage(navCon: NavController) {
    var clusterNama by remember { mutableStateOf("") }
    var clusterDeskripsi by remember { mutableStateOf("") }
    var clusterColor by remember { mutableStateOf(Color(0xFF2196F3)) }
    val events = remember { mutableStateListOf<FirstScr>() }
    var showAddEventPopUp by remember { mutableStateOf("Close") }
    var modify by remember { mutableStateOf<FirstScr?>(null) }
    val context = LocalContext.current
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(16.dp)) {
        TextButton(onClick = { navCon.popBackStack() }) { Text("Kembali") }

        TextField(
            value = clusterNama,
            onValueChange = { clusterNama = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nama Cluster") },
            placeholder = { Text("Isi Nama Cluster") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = clusterDeskripsi,
            onValueChange = { clusterDeskripsi = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Deskripsi Cluster") },
            placeholder = { Text("Isi Deskripsi Cluster") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputColorChoice(selectedColor = clusterColor, onColorChange = { clusterColor = it })

        Spacer(modifier = Modifier.height(16.dp))
        Text("Daftar Event:", style = MaterialTheme.typography.titleMedium)

        Column(modifier = Modifier.weight(1f).padding(vertical = 8.dp).verticalScroll(rememberScrollState())) {
            events.forEach { i ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Button(onClick = {
                        modify = i
                        showAddEventPopUp = "Modify"
                    }) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(i.nama)
                            Text(i.tanggal.toString())
                        }
                    }
                }
            }
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showAddEventPopUp = "Create" }
        ) {
            Text("Tambah Event")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                Log.d("DEBUG_TAM", "Tombol Save Cluster dipencet")
                scope.launch {
                    if (clusterNama.isNotBlank()) {
                        isSaving = true
                        try {
                            val newCluster = EventCluster(
                                namaCluster = clusterNama,
                                deskripsiCluster = clusterDeskripsi,
                                color = clusterColor,
                                daftarEvent = events.toList()
                            )
                            FirebaseRepository.updateCluster(newCluster)
                            Log.d("DEBUG_TAM", "Berhasil ke Firebase")

                            val existingIndex = CurrenCluster.indexOfFirst { it.namaCluster == clusterNama }
                            if (existingIndex != -1) {
                                CurrenCluster[existingIndex] = newCluster
                            } else {
                                CurrenCluster.add(newCluster)
                            }

                            Toast.makeText(context, "Cluster Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                            navCon.popBackStack()
                        } catch (e: Exception) {
                            Log.e("DEBUG_TAM", "Gagal simpan", e)
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isSaving = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = clusterNama.isNotBlank() && !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Save Cluster")
            }
        }
    }

    if (showAddEventPopUp == "Create") {
        AddEventPopUp(
            onAdd = { newEvent -> events.add(newEvent) },
            onDismiss = { showAddEventPopUp = "Close" }
        )
    } else if (showAddEventPopUp == "Modify") {
        AddEventPopUp(
            onAdd = { newEvent ->
                val index = events.indexOf(modify)
                if (index != -1) events[index] = newEvent
                else events.add(newEvent)
            },
            mod = modify,
            onDismiss = { showAddEventPopUp = "Close"; modify = null }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEventPopUp(onAdd: (FirstScr) -> Unit, mod: FirstScr? = null, onDismiss: () -> Unit) {
    var nama by remember { mutableStateOf(mod?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(mod?.deskripsi ?: "") }
    var tanggalStr by remember { mutableStateOf(mod?.tanggal?.toString() ?: LocalDate.now().toString()) }
    var img by remember { mutableStateOf<Any?>(mod?.image ?: android.R.drawable.ic_menu_gallery) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val date = try { LocalDate.parse(tanggalStr) } catch (e: Exception) { LocalDate.now() }
                onAdd(FirstScr(nama, deskripsi, date, img))
                onDismiss()
            }) { Text(if (mod == null) "Add" else "Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        },
        title = { Text(if (mod == null) "Tambah Event Baru" else "Edit Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Event") }, modifier = Modifier.fillMaxWidth())
                TextField(value = deskripsi, onValueChange = { deskripsi = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                InputDateChoice(value = tanggalStr, onValueChange = { tanggalStr = it }, label = { Text("Tanggal") })
                UploadImage(value = img, onValueChange = { img = it }, label = { Text("Gambar Event") })
            }
        }
    )
}

@Composable
fun InputColorChoice(selectedColor: Color, onColorChange: (Color) -> Unit) {
    val controller = rememberColorPickerController()
    Column {
        Text("Pilih Warna Cluster:", style = MaterialTheme.typography.titleSmall)
        HsvColorPicker(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            controller = controller,
            onColorChanged = { colorEnvelope ->
                onColorChange(colorEnvelope.color)
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(selectedColor, RoundedCornerShape(4.dp))
        )
    }
}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InputDateChoice(value: String, onValueChange: (String) -> Unit, label: @Composable () -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            onValueChange(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column {
        label()
        OutlinedButton(
            onClick = { datePickerDialog.show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(value.ifEmpty { "Pilih Tanggal" })
        }
    }
}

@Composable
fun UploadImage(value: Any?, onValueChange: (Any?) -> Unit, label: @Composable () -> Unit) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onValueChange(uri)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        label()
        AsyncImage(
            model = value,
            contentDescription = null,
            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )
        TextButton(onClick = {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }) {
            Text("Pilih Gambar")
        }
    }
}
