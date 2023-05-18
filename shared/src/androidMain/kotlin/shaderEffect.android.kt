import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shaderEffect(viewPort: MutableState<Pair<Float, Float>>): Modifier = composed {
    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f
            }
        }
    }
    Modifier.drawWithCache {
        val shader = RuntimeShader(compositeSksl)
        val shaderBrush = ShaderBrush(shader)
        shader.setFloatUniform("iResolution", size.width, size.height, density)
        shader.setFloatUniform("iTime", time)
        onDrawBehind {
            drawRoundRect(shaderBrush)
        }
    }
}