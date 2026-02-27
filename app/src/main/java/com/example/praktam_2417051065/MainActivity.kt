package com.example.praktam_2417051065

//import Model.FirstScr
import model.ScndScr
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.praktam_2417051065.ui.theme.PrakTAM_2417051065Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051065Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val firstScr = ScndScr.dummyDate[0]

    Column(modifier = Modifier.fillMaxSize().padding(30.dp)) {
        Image (
            painter = painterResource(id = firstScr.imageRes),
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )

        Text(text = "Nama: ${firstScr.nama}")
        Text(text = "deskripsi: ${firstScr.deskripsi}")
        Text(text = "tanggal: ${firstScr.tanggal}")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrakTAM_2417051065Theme {
    }
}