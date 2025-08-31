package eu.sailwithdamian.message_decoder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import eu.sailwithdamian.message_decoder.ui.DecoderScreen
import eu.sailwithdamian.message_decoder.ui.DecoderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DecoderTheme {
                DecoderScreen()
            }
        }
    }
}
