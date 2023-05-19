import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@Composable
expect fun font(
    res: String,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal,
): Font