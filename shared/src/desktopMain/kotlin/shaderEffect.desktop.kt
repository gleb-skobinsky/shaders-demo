import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.withTransform
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder


actual fun Modifier.shaderEffect(shaderOption: ShaderOptions): Modifier = composed {
    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f
            }
        }
    }
    val effect = remember(shaderOption) { RuntimeEffect.makeForShader(shaderOption.toShaderCode()) }
    val compositeShaderBuilder = remember(effect) { RuntimeShaderBuilder(effect) }
    Modifier.drawWithCache {
        compositeShaderBuilder.uniform(
            name = "iResolution",
            value1 = size.width,
            value2 = size.height
        )
        compositeShaderBuilder.uniform(
            "iTime",
            time
        )
        val shaderBrush = ShaderBrush(compositeShaderBuilder.makeShader())
        onDrawBehind {
            withTransform({ scale(scaleX = 1f, scaleY = -1f) }) {
                drawRect(shaderBrush)
            }
        }
    }
}