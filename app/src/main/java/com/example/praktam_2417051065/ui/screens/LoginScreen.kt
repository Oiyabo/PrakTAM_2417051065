package com.example.praktam_2417051065.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.praktam_2417051065.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navCon: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var isRegisterMode by remember { mutableStateOf(false) }

    // Inputs
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Setup Google Sign-In client safely
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
        isLoading = false
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                isLoading = true
                viewModel.loginWithGoogle(
                    idToken = idToken,
                    displayName = account.displayName ?: "",
                    email = account.email ?: "",
                    onSuccess = {
                        isLoading = false
                        Toast.makeText(context, "Masuk dengan Google berhasil!", Toast.LENGTH_SHORT).show()
                        navCon.popBackStack()
                    },
                    onFailure = { err ->
                        isLoading = false
                        Toast.makeText(context, "Gagal login ke Firebase: $err", Toast.LENGTH_LONG).show()
                    }
                )
            } else {
                Toast.makeText(context, "Gagal mendapatkan ID Token Google", Toast.LENGTH_LONG).show()
            }
        } catch (e: ApiException) {
            Toast.makeText(context, "Google Sign-In gagal: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isRegisterMode) "Daftar Akun Baru" else "Masuk Akun", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navCon.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card Container
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = if (isRegisterMode) "Buat Akun Anda" else "Selamat Datang Kembali",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = if (isRegisterMode) "Silakan isi data berikut untuk mendaftar." else "Silakan masuk dengan email dan kata sandi Anda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )

                        // Conditional Username Field for Register
                        AnimatedVisibility(
                            visible = isRegisterMode,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nama Pengguna (Username)") },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Surel (Email)") },
                            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Kata Sandi (Password)") },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                            trailingIcon = {
                                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Text(
                                        text = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Main Submit Button
                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank() || (isRegisterMode && username.isBlank())) {
                                    Toast.makeText(context, "Semua bidang harus diisi", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (password.length < 6) {
                                    Toast.makeText(context, "Sandi minimal harus 6 karakter", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                isLoading = true
                                if (isRegisterMode) {
                                    viewModel.registerWithEmailUsernameAndPassword(
                                        email = email.trim(),
                                        username = username.trim(),
                                        password = password,
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                                            navCon.popBackStack()
                                        },
                                        onFailure = { err ->
                                            isLoading = false
                                            Toast.makeText(context, "Registrasi gagal: $err", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                } else {
                                    viewModel.loginWithEmailAndPassword(
                                        email = email.trim(),
                                        password = password,
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(context, "Berhasil masuk!", Toast.LENGTH_SHORT).show()
                                            navCon.popBackStack()
                                        },
                                        onFailure = { err ->
                                            isLoading = false
                                            Toast.makeText(context, "Gagal masuk: $err", Toast.LENGTH_LONG).show()
                                        }
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp), // slightly taller for modern feel
                            shape = RoundedCornerShape(16.dp), // rounder
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text(if (isRegisterMode) "Daftar Sekarang" else "Masuk", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Toggle Mode Text
                        TextButton(
                            onClick = { isRegisterMode = !isRegisterMode },
                            enabled = !isLoading
                        ) {
                            Text(
                                text = if (isRegisterMode) "Sudah punya akun? Masuk disini" else "Belum punya akun? Daftar sekarang",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                    Text(" ATAU ", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
                }

                // Google Login Button
                OutlinedButton(
                    onClick = {
                        if (googleSignInClient == null) {
                            Toast.makeText(
                                context,
                                "Setup Google Sign-in belum lengkap.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@OutlinedButton
                        }
                        isLoading = true
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    enabled = !isLoading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "G  ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF4285F4) // Classic Google blue
                        )
                        Text(
                            text = "Masuk dengan Google",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
