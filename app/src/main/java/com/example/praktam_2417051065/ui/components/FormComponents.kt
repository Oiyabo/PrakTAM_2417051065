package com.example.praktam_2417051065.ui.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.model.EventCluster
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClusterSelector(cluster: EventCluster?, exp: Boolean, onExp: (Boolean) -> Unit, onSel: (EventCluster?) -> Unit, viewModel: MainViewModel, modifier: Modifier) = Box(modifier.fillMaxHeight()) {
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
