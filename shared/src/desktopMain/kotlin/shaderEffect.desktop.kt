import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

actual fun Modifier.shaderEffect(): Modifier = composed {
    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f
            }
        }
    }
    Modifier.drawWithCache {
        val effect = RuntimeEffect.makeForShader(compositeSksl)
        val compositeShaderBuilder = RuntimeShaderBuilder(effect)
        compositeShaderBuilder.uniform(
            name = "iResolution",
            value1 = size.width,
            value2 = size.height,
            value3 = density
        )
        compositeShaderBuilder.uniform(
            "iTime",
            time
        )
        val shaderBrush = ShaderBrush(compositeShaderBuilder.makeShader())
        onDrawBehind {
            drawRect(shaderBrush)
        }
    }
}