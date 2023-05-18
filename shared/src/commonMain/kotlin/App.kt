import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val viewPort: MutableState<Pair<Float, Float>> = remember { mutableStateOf(Pair(0f, 0f)) }

        Column(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    viewPort.value = Pair(it.size.width.toFloat(), it.size.height.toFloat())
                }
                .shaderEffect(viewPort)
        ) {
            Box(
                Modifier.fillMaxSize()
            ) {
                Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.weight(1f))
                    Column(Modifier.weight(4.5f)) {
                        Text(
                            text = "Don't miss our daily amazing deals.",
                            fontSize = 55.sp,
                            color = largeTextColor,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Text(
                            "Save up to 60% off your first order",
                            modifier = Modifier
                                .padding(top = 29.dp),
                            fontSize = 20.sp,
                            color = largeTextColor
                        )
                    }
                    Image(
                        painterResource("food.png"),
                        "Cover image",
                        modifier = Modifier.weight(6.5f),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

expect fun getPlatformName(): String

expect fun Modifier.shaderEffect(viewPort: MutableState<Pair<Float, Float>>): Modifier