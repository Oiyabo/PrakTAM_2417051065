package com.example.praktam_2417051065.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.model.EventCluster
import com.example.praktam_2417051065.model.EventData
import com.example.praktam_2417051065.ui.components.InputColorChoice
import com.example.praktam_2417051065.ui.components.InputDateChoice
import com.example.praktam_2417051065.ui.components.UploadImage
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddPage(navCon: NavController, viewModel: MainViewModel) {
    var clusterNama by remember { mutableStateOf("") }
    var clusterDeskripsi by remember { mutableStateOf("") }
    var clusterColor by remember { mutableStateOf(Color(0xFF2196F3)) }
    val events = remember { mutableStateListOf<EventData>() }
    val showPopup = remember { mutableStateOf(false) }
    val modify = remember { mutableStateOf<EventData?>(null) }
    val context = LocalContext.current

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
                        modify.value = i
                        showPopup.value = true
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
            onClick = {
                modify.value = null
                showPopup.value = true
            }
        ) {
            Text("Tambah Event")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (clusterNama.isNotBlank()) {
                    val newCluster = EventCluster(
                        namaCluster = clusterNama,
                        deskripsiCluster = clusterDeskripsi,
                        color = clusterColor,
                        daftarEvent = events.toList()
                    )
                    viewModel.saveCluster(newCluster)
                    Toast.makeText(context, "Cluster Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                    navCon.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = clusterNama.isNotBlank()
        ) {
            Text("Save Cluster")
        }
    }

    if (showPopup.value) {
        AddEventPopUp(
            onAdd = { newEvent ->
                val m = modify.value
                val index = if (m != null) events.indexOf(m) else -1
                if (index != -1) events[index] = newEvent
                else events.add(newEvent)
                showPopup.value = false
                modify.value = null
            },
            mod = modify.value,
            onDismiss = {
                showPopup.value = false
                modify.value = null
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddEventPopUp(onAdd: (EventData) -> Unit, mod: EventData? = null, onDismiss: () -> Unit) {
    var nama by remember { mutableStateOf(mod?.nama ?: "") }
    var deskripsi by remember { mutableStateOf(mod?.deskripsi ?: "") }
    var tanggalStr by remember { mutableStateOf(mod?.tanggal?.toString() ?: LocalDate.now().toString()) }
    var img by remember { mutableStateOf<Any?>(mod?.imageUrl ?: android.R.drawable.ic_menu_gallery) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val date = try { LocalDate.parse(tanggalStr) } catch (_: Exception) { LocalDate.now() }
                onAdd(EventData(nama, deskripsi, date, img as String))
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
