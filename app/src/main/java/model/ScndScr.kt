package model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.example.praktam_2417051065.R
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
object ScndScr {
    val dataCluster = listOf(
        EventCluster(
            namaCluster = "Pribadi",
            deskripsiCluster = "Kegiatan sehari-hari di rumah",
            color = Color(0xFF2196F3),
            daftarEvent = listOf(
                FirstScr(nama = "Kondangan", deskripsi = "Sodara nikahan", tanggal = LocalDate.of(2026, 3, 15), image = R.drawable.bluecal),
                FirstScr(nama = "Ulang tahun", deskripsi = "Adek ulang tahun", tanggal = LocalDate.of(2026, 3, 3), image = R.drawable.bluetimecal)
            )
        ),
        EventCluster(
            namaCluster = "Kuliah",
            deskripsiCluster = "Tugas dan ujian kampus",
            color = Color(0xFFF44336),
            daftarEvent = listOf(
                FirstScr(nama = "Ujian", deskripsi = "Ujian datang. tidak!!!", tanggal = LocalDate.of(2026, 3, 25), image = R.drawable.redcal),
                FirstScr(nama = "Kerja Kelompok", deskripsi = "Tempatnya jaoooh", tanggal = LocalDate.of(2026, 3, 7), image = R.drawable.redtimecal)
            )
        ),
        EventCluster(
            namaCluster = "Hobi",
            deskripsiCluster = "Waktu luang",
            color = Color(0xFF4CAF50),
            daftarEvent = listOf(
                FirstScr(nama = "Mabar", deskripsi = "asekkk", tanggal = LocalDate.of(2026, 3, 14), image = R.drawable.noimg),
                FirstScr(nama = "TIdur", deskripsi = "zzzzzzz", tanggal = LocalDate.of(2026, 3, 15), image = R.drawable.noimg),
            )
        )
    )
}