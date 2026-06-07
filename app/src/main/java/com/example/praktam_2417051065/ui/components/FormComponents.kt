package com.example.praktam_2417051065.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.data.model.EventCluster
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClusterSelector(cluster: EventCluster?, exp: Boolean, onExp: (Boolean) -> Unit, onSel: (EventCluster?) -> Unit, viewModel: MainViewModel, modifier: Modifier) = Box(modifier.fillMaxHeight()) {
    val currentAccount by viewModel.currentAccount.collectAsState()

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
        viewModel.currentCluster.forEach { c ->
            val isOwned = c.owner != null && currentAccount != null && c.owner == currentAccount?.uid
            DropdownMenuItem(
                text = {
                    Column {
                        Text(c.namaCluster, style = MaterialTheme.typography.titleSmall)
                        Text(c.deskripsiCluster, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                onClick = { onSel(c); onExp(false) },
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .then(
                        if (isOwned) Modifier.border(BorderStroke(2.dp, Color(0xFFFF9800)), RoundedCornerShape(8.dp))
                        else Modifier
                    )
            )
        }
    }
}



@Composable
fun InputColorChoice(selectedColor: Color, onColorChange: (Color) -> Unit) {
    val controller = rememberColorPickerController()

    LaunchedEffect(selectedColor) {
        controller.selectByColor(selectedColor, false)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Pilih Warna Cluster:", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HsvColorPicker(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                controller = controller,
                onColorChanged = { colorEnvelope ->
                    if (colorEnvelope.fromUser) {
                        onColorChange(colorEnvelope.color)
                    }
                }
            )
            Column(
                modifier = Modifier.width(120.dp).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(selectedColor, RoundedCornerShape(4.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                )

                var hexText by remember(selectedColor) {
                    mutableStateOf(String.format("#%06X", 0xFFFFFF and selectedColor.toArgb()))
                }

                OutlinedTextField(
                    value = hexText,
                    onValueChange = { newValue ->
                        hexText = newValue
                        try {
                            if (newValue.length == 7 || newValue.length == 9) {
                                val color = Color(android.graphics.Color.parseColor(newValue))
                                onColorChange(color)
                            }
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
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

@SuppressLint("DefaultLocale")
@Composable
fun InputTimeChoice(value: String, onValueChange: (String) -> Unit, label: @Composable () -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val formattedTime = String.format("%02d:%02d", hourOfDay, minute)
            onValueChange(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column {
        label()
        OutlinedButton(
            onClick = { timePickerDialog.show() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(value.ifEmpty { "Pilih Jam" })
        }
    }
}
