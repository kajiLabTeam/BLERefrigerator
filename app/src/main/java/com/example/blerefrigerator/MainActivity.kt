package com.example.blerefrigerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.blerefrigerator.ui.theme.BLERefrigeratorTheme
import java.nio.file.WatchEvent

var door: MutableState<Boolean> = mutableStateOf(false)
var deviceRSSI: MutableState<Int> = mutableIntStateOf(0)
const val threshold = -60

class MainActivity : ComponentActivity() {
    val getBLE = GetBLE()

    override fun onCreate(savedInstanceState: Bundle?) {
        door.value = false
        super.onCreate(savedInstanceState)
        getBLE.startScan()
        setContent {
            BLERefrigeratorTheme {
                // A surface container using the 'background' color from the theme
                Screen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        getBLE.stopScan()
    }
}

// UIもうちょいどうにかしたい
@Composable
fun Screen() {

    remember { door }
    remember { deviceRSSI }

    Column {
        Text(
            "冷蔵庫開閉検知",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Text(
                "RSSI: ${deviceRSSI.value} dBm",
                fontSize = 50.sp,
            )
            Text(
                "閾値: $threshold dBm",
                fontSize = 50.sp,
            )
        }

        // 電波強度閾値によって背景色を変化させる
        if (door.value) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xffFF7777)
            ) {
                GreetingText(
                    message = "開いている"
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Cyan
            ) {
                GreetingText(
                    message = "閉じている"
                )
            }
        }

    }
}

@Composable
fun GreetingText(message: String) {
    Box(contentAlignment = Alignment.Center) {
        Text(
            text = message,
            fontSize = 70.sp,
            lineHeight = 116.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BirthdayCardPreview() {
    BLERefrigeratorTheme {
        GreetingText(message = "Happy Birthday Sam!")
    }
}