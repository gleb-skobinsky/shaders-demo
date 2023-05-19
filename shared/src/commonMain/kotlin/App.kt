import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

enum class ShaderOptions(name: String) {
    CLOUDS("Clouds shader"),
    FUJI("Cyberpunk Fuji shader"),
    PLANET("Planet shader");

    fun toShaderCode() = when (this) {
        CLOUDS -> compositeSksl
        FUJI -> sksl2
        PLANET -> sksl3
    }

    fun toIconColor() = when (this) {
        CLOUDS -> Color.White
        FUJI -> Color.Magenta
        PLANET -> Color.White
    }
}

expect val largeTextSize: TextUnit

expect val iconPadding: Dp

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
        val fontFamily = FontFamily(font("quicksand"))
        val chosenShader = remember { mutableStateOf(ShaderOptions.CLOUDS) }

        Column(
            Modifier
                .fillMaxSize()
                .shaderEffect(chosenShader.value)
        ) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                Column(Modifier.weight(4.5f)) {
                    Text(
                        text = "Don't miss our daily amazing deals.",
                        fontSize = largeTextSize,
                        color = chosenShader.value.toIconColor(),
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontFamily = fontFamily
                    )
                    Text(
                        "Save up to 60% off your first order",
                        modifier = Modifier
                            .padding(top = 29.dp),
                        fontSize = 20.sp,
                        color = chosenShader.value.toIconColor(),
                        fontFamily = fontFamily
                    )
                    Row(Modifier.padding(vertical = 32.dp)) {
                        TextField(
                            value = textFieldValue,
                            onValueChange = { textFieldValue = it },
                            modifier = Modifier.height(52.dp),
                            shape = RectangleShape,
                            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                            leadingIcon = {
                                Icon(Icons.Outlined.Send, "Email icon", tint = Color.Gray)
                            },
                            placeholder = {
                                Text(
                                    text = "Enter your email address",
                                    fontFamily = fontFamily,
                                    color = Color.Gray
                                )
                            }
                        )
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                            modifier = Modifier.height(52.dp),
                            onClick = {}
                        ) {
                            Text("Subscribe", color = Color.White, fontFamily = fontFamily, fontSize = 14.sp)
                        }
                    }
                    Text("Choose a shader:", modifier = Modifier.padding(vertical = 24.dp), fontFamily = fontFamily, color = chosenShader.value.toIconColor())
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        ShaderChoiceButton(chosenShader, fontFamily, ShaderOptions.CLOUDS)
                        Spacer(Modifier.width(24.dp))
                        ShaderChoiceButton(chosenShader, fontFamily, ShaderOptions.FUJI)
                        Spacer(Modifier.width(24.dp))
                        ShaderChoiceButton(chosenShader, fontFamily, ShaderOptions.PLANET)
                    }
                }
                Icon(
                    painterResource("github.png"),
                    "Main icon",
                    Modifier.fillMaxHeight().weight(6.5f).background(Color.Transparent).padding(iconPadding),
                    tint = chosenShader.value.toIconColor()
                )
            }
        }
    }
}

@Composable
fun ShaderChoiceButton(chosenShader: MutableState<ShaderOptions>, fontFamily: FontFamily, choice: ShaderOptions) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        modifier = Modifier.height(52.dp),
        onClick = {
            chosenShader.value = choice
        }
    ) {
        Text(choice.name, color = Color.White, fontFamily = fontFamily, fontSize = 14.sp)
    }
}

expect fun getPlatformName(): String

expect fun Modifier.shaderEffect(shaderOption: ShaderOptions): Modifier