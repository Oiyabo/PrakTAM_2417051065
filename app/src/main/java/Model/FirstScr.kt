package Model
import androidx.annotation.DrawableRes

data class FirstScr (
    val nama: String,
    val deskripsi: String,
    val tanggal: Int,
    @DrawableRes val imageRes: Int
)