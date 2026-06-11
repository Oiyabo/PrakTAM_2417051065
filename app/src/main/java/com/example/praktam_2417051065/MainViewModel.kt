package com.example.praktam_2417051065

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2417051065.data.model.EventCluster
import com.example.praktam_2417051065.data.model.EventData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import com.example.praktam_2417051065.data.repo.UserPreferences
import com.example.praktam_2417051065.data.repo.SavedAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository()
    val userPreferences = UserPreferences(application)
    private val auth = FirebaseAuth.getInstance()
    
    private val _currentCluster = mutableStateListOf<EventCluster>()
    val currentCluster: List<EventCluster> get() = _currentCluster

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _currentAccount = MutableStateFlow<SavedAccount?>(userPreferences.getCurrentAccount())
    val currentAccount: StateFlow<SavedAccount?> = _currentAccount.asStateFlow()

    private val _accounts = MutableStateFlow<List<SavedAccount>>(userPreferences.getAccounts())
    val accounts: StateFlow<List<SavedAccount>> = _accounts.asStateFlow()

    var selectedClusterToEdit: EventCluster? = null
    var selectedEventToHighlight: EventData? = null
    
    var selectedClusterForFilter by androidx.compose.runtime.mutableStateOf<EventCluster?>(null)

    // Set the currently active local session (synchronously or manually)
    fun setCurrentAccount(account: SavedAccount?) {
        userPreferences.setCurrentAccount(account)
        _currentAccount.value = account
        _accounts.value = userPreferences.getAccounts()
        if (account == null) {
            auth.signOut()
        }
    }

    fun removeAccount(account: SavedAccount) {
        userPreferences.removeAccount(account.uid)
        _accounts.value = userPreferences.getAccounts()
        
        val current = _currentAccount.value
        if (current?.uid == account.uid) {
            auth.signOut()
            _currentAccount.value = null
        }
    }

    fun registerWithEmailUsernameAndPassword(
        email: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            val savedAccount = SavedAccount(
                                username = username,
                                email = email,
                                uid = firebaseUser.uid,
                                type = "EMAIL",
                                password = password
                            )
                            userPreferences.setCurrentAccount(savedAccount)
                            _currentAccount.value = savedAccount
                            _accounts.value = userPreferences.getAccounts()
                            onSuccess()
                        }
                } else {
                    onFailure("Registrasi berhasil, tetapi gagal mendapatkan user.")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Registrasi gagal")
            }
    }

    fun loginWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val savedAccount = SavedAccount(
                        username = firebaseUser.displayName ?: email.substringBefore("@"),
                        email = email,
                        uid = firebaseUser.uid,
                        type = "EMAIL",
                        password = password
                    )
                    userPreferences.setCurrentAccount(savedAccount)
                    _currentAccount.value = savedAccount
                    _accounts.value = userPreferences.getAccounts()
                    onSuccess()
                } else {
                    onFailure("Login berhasil, tetapi gagal mendapatkan user.")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Login gagal")
            }
    }

    fun loginWithGoogle(
        idToken: String,
        displayName: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val savedAccount = SavedAccount(
                        username = firebaseUser.displayName ?: displayName,
                        email = firebaseUser.email ?: email,
                        uid = firebaseUser.uid,
                        type = "GOOGLE",
                        password = null
                    )
                    userPreferences.setCurrentAccount(savedAccount)
                    _currentAccount.value = savedAccount
                    _accounts.value = userPreferences.getAccounts()
                    onSuccess()
                } else {
                    onFailure("Gagal mendapatkan info user dari Firebase")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Autentikasi Firebase gagal")
            }
    }

    fun switchAccount(
        savedAccount: SavedAccount,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (savedAccount.type == "EMAIL") {
            val pwd = savedAccount.password
            if (pwd != null) {
                auth.signInWithEmailAndPassword(savedAccount.email, pwd)
                    .addOnSuccessListener {
                        userPreferences.setCurrentAccount(savedAccount)
                        _currentAccount.value = savedAccount
                        _accounts.value = userPreferences.getAccounts()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e.message ?: "Gagal masuk ke akun")
                    }
            } else {
                onFailure("Sandi akun tidak tersimpan. Silakan login kembali.")
            }
        } else {
            // Google accounts require UI launcher
            onFailure("GOOGLE_TRIGGER")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchClusters() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val account = _currentAccount.value
                val fetched = if (account != null) {
                    repository.fetchFromFirestore(account.uid)
                } else {
                    emptyList()
                }
                _currentCluster.clear()
                _currentCluster.addAll(fetched)
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveClusterLocal(newCluster: EventCluster) {
        val existingIndex = _currentCluster.indexOfFirst { it.id == newCluster.id }
        if (existingIndex != -1) {
            _currentCluster[existingIndex] = newCluster
        } else {
            _currentCluster.add(newCluster)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveClusterToFirestore(newCluster: EventCluster): Boolean {
        return repository.saveClusterToFirestore(newCluster)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addClusterFromShareCode(
        shareCode: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val account = _currentAccount.value
        if (account == null) {
            onFailure("Harap login terlebih dahulu.")
            return
        }

        viewModelScope.launch {
            val existingLocal = _currentCluster.find { it.id == shareCode }
            if (existingLocal != null) {
                if (existingLocal.owner == account.uid || existingLocal.viewers.contains(account.uid)) {
                    onFailure("Cluster ini sudah ada di daftar Anda.")
                    return@launch
                }
            }

            try {
                val clusterFromDb = repository.getClusterById(shareCode)
                if (clusterFromDb != null) {
                    val success = repository.addViewerToCluster(shareCode, account.uid)
                    if (success) {
                        // Create a local copy with the updated viewers list so it reflects immediately
                        val updatedViewers = clusterFromDb.viewers.toMutableList()
                        updatedViewers.add(account.uid)
                        val updatedCluster = clusterFromDb.copy(viewers = updatedViewers)
                        
                        saveClusterLocal(updatedCluster)
                        onSuccess()
                    } else {
                        onFailure("Gagal menambahkan Anda ke cluster.")
                    }
                } else {
                    onFailure("Cluster tidak ditemukan.")
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Terjadi kesalahan.")
            }
        }
    }

    suspend fun uploadImageToStorage(uri: Uri, clusterName: String, eventName: String): String? {
        return repository.uploadImageToStorage(uri, clusterName, eventName)
    }
}
