package model
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class FirstScr (
    val nama: String,
    val deskripsi: String,
    val tanggal: LocalDate,
    val image: Any? // Can be DrawableRes (Int) or Uri (String/Uri)
)

@RequiresApi(Build.VERSION_CODES.O)
data class EventCluster(
    val namaCluster: String,
    val deskripsiCluster: String,
    val daftarEvent: List<FirstScr>,
    val color: androidx.compose.ui.graphics.Color
)