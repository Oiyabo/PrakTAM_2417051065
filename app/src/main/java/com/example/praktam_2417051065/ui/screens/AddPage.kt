package com.example.praktam_2417051065.ui.screens

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.data.model.EventCluster
import com.example.praktam_2417051065.data.model.EventData
import com.example.praktam_2417051065.ui.components.InputColorChoice
import com.example.praktam_2417051065.ui.components.InputDateChoice
import com.example.praktam_2417051065.ui.components.UploadImage
import com.example.praktam_2417051065.ui.components.AddEventPopUp
import com.example.praktam_2417051065.ui.components.EventRowCard
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddPage(navCon: NavController, viewModel: MainViewModel) {
    val clusterToEdit = viewModel.selectedClusterToEdit
    val isEditing = clusterToEdit != null

    var clusterNama by remember { mutableStateOf(clusterToEdit?.namaCluster ?: "") }
    var clusterDeskripsi by remember { mutableStateOf(clusterToEdit?.deskripsiCluster ?: "") }
    var clusterColor by remember { mutableStateOf(clusterToEdit?.color ?: Color(0xFFFFB74D)) }
    val events = remember { mutableStateListOf<EventData>().apply { 
        clusterToEdit?.daftarEvent?.let { list ->
            val highlighted = viewModel.selectedEventToHighlight
            if (highlighted != null) {
                val match = list.find { it.nama == highlighted.nama && it.tanggal == highlighted.tanggal }
                if (match != null) {
                    add(match)
                    addAll(list.filter { it != match })
                } else {
                    addAll(list)
                }
            } else {
                addAll(list)
            }
        } 
    } }
    val showPopup = remember { mutableStateOf(false) }
    val modify = remember { mutableStateOf<EventData?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    
    val currentAccount by viewModel.currentAccount.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.selectedEventToHighlight = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Cluster" else "Tambah Cluster Baru") },
                navigationIcon = {
                    IconButton(onClick = { navCon.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Detail Cluster", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = clusterNama,
                        onValueChange = { clusterNama = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nama Cluster") },
                        placeholder = { Text("Contoh: Rapat Mingguan") },
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isEditing // Do not allow changing cluster name since it's the document ID in Firestore
                    )
                    OutlinedTextField(
                        value = clusterDeskripsi,
                        onValueChange = { clusterDeskripsi = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Deskripsi Cluster") },
                        placeholder = { Text("Isi deskripsi singkat") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    InputColorChoice(selectedColor = clusterColor, onColorChange = { clusterColor = it })
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Daftar Event (${events.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    if (events.isEmpty()) {
                        Text("Belum ada event ditambahkan.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        val highlighted = viewModel.selectedEventToHighlight
                        val hasHighlight = highlighted != null && events.any { it.nama == highlighted.nama && it.tanggal == highlighted.tanggal }
                        
                        if (hasHighlight) {
                            val topEvent = events.firstOrNull { it.nama == highlighted?.nama && it.tanggal == highlighted?.tanggal }
                            if (topEvent != null) {
                                EventRowCard(
                                    e = topEvent,
                                    onEdit = {
                                        modify.value = topEvent
                                        showPopup.value = true
                                    },
                                    onDelete = {
                                        events.remove(topEvent)
                                    },
                                    isHighlighted = true
                                )
                                
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                            
                            events.filter { !(it.nama == highlighted?.nama && it.tanggal == highlighted?.tanggal) }.forEach { e ->
                                EventRowCard(
                                    e = e,
                                    onEdit = {
                                        modify.value = e
                                        showPopup.value = true
                                    },
                                    onDelete = {
                                        events.remove(e)
                                    }
                                )
                            }
                        } else {
                            events.forEach { e ->
                                EventRowCard(
                                    e = e,
                                    onEdit = {
                                        modify.value = e
                                        showPopup.value = true
                                    },
                                    onDelete = {
                                        events.remove(e)
                                    }
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            modify.value = null
                            showPopup.value = true
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ Tambah Event")
                    }
                }
            }

            Button(
                onClick = {
                    if (clusterNama.isNotBlank() && !isSaving) {
                        isSaving = true
                        coroutineScope.launch {
                            var uploadFailed = false
                            val finalizedEvents = events.map { event ->
                                val isLocal = event.imageUrl.startsWith("content://") || event.imageUrl.startsWith("file://")
                                if (isLocal) {
                                    val localUri = Uri.parse(event.imageUrl)
                                    val downloadUrl = viewModel.uploadImageToStorage(localUri, clusterNama, event.nama)
                                    if (downloadUrl != null) {
                                        event.copy(imageUrl = downloadUrl)
                                    } else {
                                        uploadFailed = true
                                        event
                                    }
                                } else {
                                    event
                                }
                            }

                            if (uploadFailed) {
                                Toast.makeText(context, "Gagal mengunggah gambar ke Storage.", Toast.LENGTH_LONG).show()
                                isSaving = false
                                return@launch
                            }

                            val newCluster = EventCluster(
                                id = clusterToEdit?.id ?: java.util.UUID.randomUUID().toString(),
                                namaCluster = clusterNama,
                                deskripsiCluster = clusterDeskripsi,
                                color = clusterColor,
                                daftarEvent = finalizedEvents,
                                owner = clusterToEdit?.owner ?: currentAccount?.uid
                            )
                            val saveSuccess = viewModel.saveClusterToFirestore(newCluster)
                            if (saveSuccess) {
                                viewModel.saveClusterLocal(newCluster)
                                Toast.makeText(context, "Cluster Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                                navCon.popBackStack()
                            } else {
                                Toast.makeText(context, "Gagal menyimpan cluster", Toast.LENGTH_LONG).show()
                            }
                            isSaving = false
                        }
                    } else if (clusterNama.isBlank()) {
                        Toast.makeText(context, "Nama Cluster tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSaving,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Simpan Cluster")
                }
            }
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


