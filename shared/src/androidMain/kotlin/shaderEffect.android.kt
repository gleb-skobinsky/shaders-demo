import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.withTransform

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun Modifier.shaderEffect(shaderOption: ShaderOptions): Modifier = composed {
    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f
            }
        }
    }
    Modifier.drawWithCache {
        val shader = RuntimeShader(shaderOption.toShaderCode())
        val shaderBrush = ShaderBrush(shader)
        shader.setFloatUniform("iResolution", size.width, size.height)
        shader.setFloatUniform("iTime", time)
        onDrawBehind {
            withTransform({ scale(scaleX = 1f, scaleY = -1f) }) {
                drawRect(shaderBrush)
            }
        }
    }
}