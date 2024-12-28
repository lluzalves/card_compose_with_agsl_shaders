package com.daniel.compose.shadersexperimentsapp.presentation.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import com.daniel.compose.shadersexperimentsapp.agsl.SOLAR_FLAIR_SHADER
import com.daniel.compose.shadersexperimentsapp.customDeepBlue
import com.daniel.compose.shadersexperimentsapp.presentation.ui.theme.PurpleGrey40
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A collection of extensions functions
 */

fun Modifier.solarFlareShaderBackground(
    baseColor: Color,
    backgroundColor: Color,
): Modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    this.then(SolarFlareShaderBackgroundElement(baseColor, backgroundColor))
} else {
    this.then(Modifier.simpleGradient())
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private data class SolarFlareShaderBackgroundElement(
    val baseColor: Color,
    val backgroundColor: Color,
) : ModifierNodeElement<SolarFlairShaderBackgroundNode>() {
    override fun create() = SolarFlairShaderBackgroundNode(baseColor, backgroundColor)
    override fun update(node: SolarFlairShaderBackgroundNode) {
        node.updateColors(baseColor, backgroundColor)
    }
}

fun Modifier.simpleGradient(): Modifier = drawWithCache {
    val gradientBrush = Brush.verticalGradient(listOf(Blue, PurpleGrey40, customDeepBlue))
    onDrawBehind {
        drawRect(gradientBrush, alpha = 1f)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class SolarFlairShaderBackgroundNode(
    baseColor: Color,
    backgroundColor: Color,
) : DrawModifierNode, Modifier.Node() {
    private val shader = RuntimeShader(SOLAR_FLAIR_SHADER)
    private val shaderBrush = ShaderBrush(shader)
    private val time = mutableFloatStateOf(0f)

    init {
        updateColors(baseColor, backgroundColor)
    }

    fun updateColors(baseColor: Color, backgroundColor: Color) {
        shader.setColorUniform(
            "baseColor", android.graphics.Color.valueOf(
                baseColor.red, baseColor.green, baseColor.blue, baseColor.alpha
            )
        )
        shader.setColorUniform(
            "backgroundColor", android.graphics.Color.valueOf(
                backgroundColor.red,
                backgroundColor.green,
                backgroundColor.blue,
                backgroundColor.alpha
            )
        )
    }

    override fun ContentDrawScope.draw() {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time.floatValue)

        drawRect(shaderBrush)
        drawContent()
    }

    override fun onAttach() {
        coroutineScope.launch {
            while (isAttached) {
                delay(150)
                withInfiniteAnimationFrameMillis {
                    time.floatValue = it / 3000f
                }
            }
        }
    }
}

enum class Axis {
    Horizontal, Vertical
}
