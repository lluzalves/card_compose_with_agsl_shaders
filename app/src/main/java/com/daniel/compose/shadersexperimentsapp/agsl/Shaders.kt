package com.daniel.compose.shadersexperimentsapp.agsl

import org.intellij.lang.annotations.Language

@Language("AGSL")
val SOLAR_FLAIR_SHADER = """
    uniform float2 resolution;
    uniform float time;
    layout(color) uniform half4 baseColor;
    layout(color) uniform half4 backgroundColor;
    
    const int ITERATIONS = 1;
    const float INTENSITY = 100.0;
    const float TIME_MULTIPLIER = 0.25;
    
    float4 main(in float2 fragCoord) {
        // Slow down the animation to be more soothing
        float calculatedTime = time * TIME_MULTIPLIER;
        
        // Coords
        float2 uv = fragCoord / resolution.xy;
        float2 uvCalc = (uv * 6.0) - (INTENSITY * 1.0);
        
        // Values to adjust per iteration
        float2 iterationChange = float2(uvCalc);
        float colorPart = 1.0;
        
        for (int i = 0; i < ITERATIONS; i++) {
            iterationChange = uvCalc + float2(
                cos(calculatedTime + iterationChange.x) +
                sin(calculatedTime - iterationChange.y), 
                cos(calculatedTime - iterationChange.x) +
                sin(calculatedTime + iterationChange.y) 
            );
            colorPart += 0.8 / length(
                float2(uvCalc.x / (cos(iterationChange.x + calculatedTime) * INTENSITY),
                    uvCalc.y / (sin(iterationChange.y + calculatedTime) * INTENSITY)
                )
            );
        }
        colorPart = 2.0 - (colorPart / float(ITERATIONS));
        
        // Fade out the bottom on a curve
        float mixRatio = 1.0 - (uv.y * uv.y);
        // Mix calculated color with the incoming base color
        float4 color = float4(colorPart * baseColor.r, colorPart * baseColor.g, colorPart * baseColor.b, 1.0);
        // Mix color with the background
        color = float4(
            mix(backgroundColor.r, color.r, mixRatio),
            mix(backgroundColor.g, color.g, mixRatio),
            mix(backgroundColor.b, color.b, mixRatio),
            1.0
        );
        // Keep all channels within valid bounds of 0.0 and 1.0
        return clamp(color, 0.0, 1.0);
    }""".trimIndent()