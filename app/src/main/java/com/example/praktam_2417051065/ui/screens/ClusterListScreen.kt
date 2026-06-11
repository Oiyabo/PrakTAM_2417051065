package com.example.praktam_2417051065.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.data.model.EventCluster

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClusterListScreen(navCon: NavController, viewModel: MainViewModel) {
    var shareCodeInput by remember { mutableStateOf("") }
    var highlightedCluster by remember { mutableStateOf<EventCluster?>(null) }
    var isLoadingAdd by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val currentAccount by viewModel.currentAccount.collectAsState()
    
    // Only show clusters owned by the current user
    val userClusters = viewModel.currentCluster.filter { 
        it.owner != null && currentAccount != null && it.owner == currentAccount?.uid 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Cluster") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top section: Add by Share Code
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = shareCodeInput,
                    onValueChange = { shareCodeInput = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Share Code") },
                    placeholder = { Text("Masukkan kode cluster") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                
                Button(
                    onClick = {
                        if (shareCodeInput.isNotBlank()) {
                            isLoadingAdd = true
                            viewModel.addClusterFromShareCode(
                                shareCode = shareCodeInput.trim(),
                                onSuccess = {
                                    isLoadingAdd = false
                                    shareCodeInput = ""
                                    Toast.makeText(context, "Cluster berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { msg ->
                                    isLoadingAdd = false
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Kode tidak boleh kosong", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoadingAdd
                ) {
                    if (isLoadingAdd) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Add")
                    }
                }
            }
            
            HorizontalDivider()
            
            // Middle section: Cluster List
            Text("Cluster Anda", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            if (userClusters.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Belum ada cluster. Buat baru atau tambahkan dari Share Code.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Option to show "All Clusters" (No filter)
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectedClusterForFilter = null
                                    navCon.popBackStack()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (viewModel.selectedClusterForFilter == null) 
                                    MaterialTheme.colorScheme.primaryContainer 
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                "Tampilkan Semua Event", 
                                modifier = Modifier.padding(16.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    items(userClusters) { cluster ->
                        val isHighlighted = highlightedCluster?.id == cluster.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { highlightedCluster = cluster },
                                    onDoubleClick = {
                                        viewModel.selectedClusterForFilter = cluster
                                        navCon.popBackStack()
                                    }
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = if (isHighlighted) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                                     else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = cluster.color,
                                    modifier = Modifier.size(40.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(cluster.namaCluster, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(
                                        cluster.deskripsiCluster.ifEmpty { "Tanpa deskripsi" }, 
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1, 
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Bottom section: Share Code of highlighted cluster
            if (highlightedCluster != null) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Share Code: ${highlightedCluster?.namaCluster}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = highlightedCluster?.id ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Button(
                                onClick = {
                                    highlightedCluster?.id?.let {
                                        clipboardManager.setText(AnnotatedString(it))
                                        Toast.makeText(context, "Share Code disalin", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Copy")
                            }
                        }
                        Text("Bagikan kode ini agar orang lain dapat menambahkan cluster ini.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }
            } else if (userClusters.isNotEmpty()) {
                Text(
                    "Klik sekali pada cluster untuk melihat Share Code.\nKlik ganda (double-click) untuk memfilter kalender.", 
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
