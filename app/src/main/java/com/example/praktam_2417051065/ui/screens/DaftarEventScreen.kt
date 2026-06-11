package com.example.praktam_2417051065.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.UiState
import com.example.praktam_2417051065.data.model.DataSource
import com.example.praktam_2417051065.data.model.EventCluster
import com.example.praktam_2417051065.data.model.EventData
import com.example.praktam_2417051065.ui.components.*
import java.time.LocalDate
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.font.FontWeight
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DaftarEventScreen(navCon: NavController, viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val currentAccount by viewModel.currentAccount.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    
    LaunchedEffect(currentAccount) {
        viewModel.fetchClusters()
    }

    var focusOnDate by remember { mutableIntStateOf(1) }
    var calCollapse by remember { mutableStateOf(true) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val selectedEvent = remember { mutableStateOf<EventData?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val placeholderImage = DataSource.getResourceId(context, "noimg")

    // Setup Google Sign-In launcher for account switching
    val webClientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
    val googleSignInClient = remember(webClientIdResId) {
        if (webClientIdResId != 0) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(webClientIdResId))
                .requestEmail()
                .build()
            GoogleSignIn.getClient(context, gso)
        } else {
            null
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                viewModel.loginWithGoogle(
                    idToken = idToken,
                    displayName = account.displayName ?: "",
                    email = account.email ?: "",
                    onSuccess = {
                        Toast.makeText(context, "Beralih ke akun Google berhasil!", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = { err ->
                        Toast.makeText(context, "Gagal login: $err", Toast.LENGTH_LONG).show()
                    }
                )
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In gagal: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    val displayEvents = remember(viewModel.selectedClusterForFilter, selectedDate, focusOnDate, viewModel.currentCluster.size) {
        val base = viewModel.selectedClusterForFilter?.daftarEvent ?: viewModel.currentCluster.flatMap { it.daftarEvent }
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

    val isEventOwned: (EventData) -> Boolean = remember(viewModel.currentCluster.size, currentAccount) {
        { event ->
            val ownerUid = viewModel.currentCluster.find { cluster ->
                cluster.daftarEvent.any { e -> e.nama == event.nama && e.tanggal == event.tanggal }
            }?.owner
            ownerUid != null && currentAccount != null && ownerUid == currentAccount?.uid
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentAccount != null) "Halo, ${currentAccount?.username}!" else "Halo, Tamu!") },
                actions = {
                    IconButton(onClick = { showAccountSheet = true }) {
                        Icon(Icons.Filled.AccountCircle, contentDescription = "Akun")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        navCon.navigate("clusterList")
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f)
                ) {
                    val clusterName = viewModel.selectedClusterForFilter?.namaCluster ?: "All Clusters"
                    Text(clusterName, modifier = Modifier.padding(horizontal = 16.dp), maxLines = 1, fontWeight = FontWeight.Bold)
                }

                if (viewModel.selectedClusterForFilter != null && viewModel.selectedClusterForFilter?.owner != null && currentAccount != null && viewModel.selectedClusterForFilter?.owner == currentAccount?.uid) {
                    FloatingActionButton(
                        onClick = {
                            viewModel.selectedClusterToEdit = viewModel.selectedClusterForFilter
                            navCon.navigate("addPage")
                        },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(painterResource(android.R.drawable.ic_menu_edit), contentDescription = "Edit Cluster")
                    }
                }
                
                FloatingActionButton(
                    onClick = { calCollapse = !calCollapse },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (calCollapse) android.R.drawable.ic_menu_today 
                                 else android.R.drawable.ic_menu_my_calendar
                        ),
                        contentDescription = if (calCollapse) "Tampilkan Kalender" else "Sembunyikan Kalender"
                    )
                }

                FloatingActionButton(
                    onClick = { 
                        if (currentAccount != null) {
                            viewModel.selectedClusterToEdit = null
                            navCon.navigate("addPage") 
                        } else {
                            navCon.navigate("loginPage")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah Event", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (uiState) {
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("Memuat data...", color = MaterialTheme.colorScheme.secondary)
                    }
                }
                is UiState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.stat_notify_error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Gagal memuat data", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchClusters() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
                is UiState.Success -> {
                    Column(Modifier.fillMaxSize().padding(8.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                            if (calCollapse) {
                                CollapsedCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, viewModel)
                            } else {
                                MonthlyCalendar(selectedDate, { selectedDate = it }, { focusOnDate = it }, viewModel, Modifier.padding(bottom = 8.dp))
                            }
                        }

                        Box(Modifier.weight(1f).fillMaxSize()) {
                            androidx.compose.animation.Crossfade(
                                targetState = displayEvents.isEmpty(),
                                label = "EventListCrossfade"
                            ) { isEmpty ->
                                if (isEmpty) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                painter = painterResource(android.R.drawable.ic_menu_today),
                                                contentDescription = "No Events",
                                                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                                                modifier = Modifier.size(64.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Belum ada event", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.bodyLarge)
                                            Text("Pilih tanggal lain atau tambahkan event", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                        }
                                    }
                                } else {
                                    EventListElegant(displayEvents, placeholderImage, isEventOwned) { selectedEvent.value = it }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedEvent.value?.let { event ->
        val parentCluster = remember(event, viewModel.currentCluster.size) {
            viewModel.currentCluster.find { cluster ->
                cluster.daftarEvent.any { e -> e.nama == event.nama && e.tanggal == event.tanggal }
            }
        }
        val isOwner = parentCluster != null && parentCluster.owner != null && currentAccount != null && parentCluster.owner == currentAccount?.uid

        ShowDetailedEventInfo(
            e = event,
            placeholder = placeholderImage,
            isOwner = isOwner,
            onEditCluster = {
                viewModel.selectedClusterToEdit = parentCluster
                viewModel.selectedEventToHighlight = event
                navCon.navigate("addPage")
            },
            onDismiss = { selectedEvent.value = null }
        )
    }

    if (showAccountSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAccountSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Pilih Akun", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

                if (accounts.isEmpty()) {
                    Text("Belum ada akun di perangkat ini.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    accounts.forEach { account ->
                        val isActive = currentAccount != null && account.uid == currentAccount?.uid
                        Card(
                            onClick = { 
                                viewModel.switchAccount(
                                    savedAccount = account,
                                    onSuccess = {
                                        showAccountSheet = false
                                        Toast.makeText(context, "Berhasil beralih ke ${account.username}", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = { err ->
                                        if (err == "GOOGLE_TRIGGER") {
                                            showAccountSheet = false
                                            if (googleSignInClient != null) {
                                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                            } else {
                                                Toast.makeText(context, "Google Sign-in belum dikonfigurasi", Toast.LENGTH_LONG).show()
                                            }
                                        } else {
                                            Toast.makeText(context, err, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(account.username, fontWeight = FontWeight.Bold)
                                        Spacer(Modifier.width(8.dp))
                                        SuggestionChip(
                                            onClick = { },
                                            label = { Text(account.type, fontSize = 10.sp) },
                                            modifier = Modifier.height(20.dp)
                                        )
                                    }
                                    Text(account.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isActive) {
                                        Icon(Icons.Filled.Check, contentDescription = "Aktif", tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    IconButton(onClick = { viewModel.removeAccount(account) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Hapus Akun", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        showAccountSheet = false
                        navCon.navigate("loginPage")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Tambah Akun Baru")
                }
            }
        }
    }
}
