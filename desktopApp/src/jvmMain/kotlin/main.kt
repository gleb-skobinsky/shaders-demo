import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        title = "Grocery Store",
        onCloseRequest = ::exitApplication,
        state = WindowState(WindowPlacement.Maximized)
    ) {
        MainView()
    }
}