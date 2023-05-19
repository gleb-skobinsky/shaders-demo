import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }

        Column(
            Modifier
                .fillMaxSize()
                .shaderEffect()
        ) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                Column(Modifier.weight(4.5f)) {
                    Text(
                        text = "Don't miss our daily amazing deals.",
                        fontSize = 55.sp,
                        color = largeTextColor,
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontFamily = fontFamily
                    )
                    Text(
                        "Save up to 60% off your first order",
                        modifier = Modifier
                            .padding(top = 29.dp),
                        fontSize = 20.sp,
                        color = largeTextColor,
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

expect fun getPlatformName(): String

expect fun Modifier.shaderEffect(): Modifier