import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

actual fun getPlatformName(): String = "Android"

@Composable
fun MainView() = App()

actual val largeTextSize: TextUnit = 24.sp

actual val iconPadding: Dp = 30.dp