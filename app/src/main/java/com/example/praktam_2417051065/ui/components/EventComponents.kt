package com.example.praktam_2417051065.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.praktam_2417051065.model.DataSource
import com.example.praktam_2417051065.model.EventData

@Composable
fun ShowDetailedEventInfo(e: EventData, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val placeholder = remember { DataSource.getResourceId(context, "noimg") }
    val imageModel = remember(e.image) {
        if (e.image is String && !e.image.startsWith("http") && !e.image.startsWith("content")) {
            DataSource.getResourceId(context, e.image)
        } else {
            e.image ?: placeholder
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Tutup") } },
        title = { Text(e.nama, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                AsyncImage(
                    model = imageModel,
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
}

@Composable
fun DetailScreenBig(events: List<EventData>, placeholder: Int, onClick: (EventData) -> Unit) {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { e ->
            val imageModel = remember(e.image) {
                if (e.image is String && !e.image.startsWith("http") && !e.image.startsWith("content")) {
                    DataSource.getResourceId(context, e.image)
                } else {
                    e.image ?: placeholder
                }
            }
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = null,
                        modifier = Modifier.size(180.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(e.nama, style = MaterialTheme.typography.titleLarge)
                    Text(e.deskripsi, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    Text(
                        "Tanggal: ${e.tanggal}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = { onClick(e) },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) {
                        Text("Detail")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreenMed(events: List<EventData>, placeholder: Int, onClick: (EventData) -> Unit) {
    val context = LocalContext.current
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { e ->
            val imageModel = remember(e.image) {
                if (e.image is String && !e.image.startsWith("http") && !e.image.startsWith("content")) {
                    DataSource.getResourceId(context, e.image)
                } else {
                    e.image ?: placeholder
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                    .clickable { onClick(e) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(Modifier.padding(start = 12.dp).weight(1f)) {
                    Text(e.nama, style = MaterialTheme.typography.titleMedium)
                    Text(e.deskripsi, maxLines = 1, style = MaterialTheme.typography.bodyMedium)
                    Text("Tgl: ${e.tanggal}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun DetailScreenSmall(events: List<EventData>, placeholder: Int, onClick: (EventData) -> Unit) {
    val context = LocalContext.current
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { e ->
            val imageModel = remember(e.image) {
                if (e.image is String && !e.image.startsWith("http") && !e.image.startsWith("content")) {
                    DataSource.getResourceId(context, e.image)
                } else {
                    e.image ?: placeholder
                }
            }
            Box(
                Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onClick(e) }
            ) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.4f)))
                Column(Modifier.fillMaxSize().padding(4.dp), Arrangement.SpaceBetween, Alignment.CenterHorizontally) {
                    Text(e.nama, color = Color.White, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1)
                    Text(e.tanggal.toString(), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
