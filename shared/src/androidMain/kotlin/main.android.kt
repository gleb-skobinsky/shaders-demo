import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = App()
