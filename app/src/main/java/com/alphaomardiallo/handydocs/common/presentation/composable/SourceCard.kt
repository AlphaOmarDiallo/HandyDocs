package com.alphaomardiallo.handydocs.common.presentation.composable

import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun SourceCard(
    modifier: Modifier = Modifier,
    index: Int = 0,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick.invoke() }
            .graphicsLayer {
                val density = Resources.getSystem().displayMetrics.density
                // Create a true 3D effect
                rotationX = 5f
                // Each card tilts slightly differently for dynamic appearance
                rotationY = if (index == 0) -2f else 2f
                // Apply subtle scale to enhance 3D feeling
                scaleX = 0.98f
                scaleY = 0.98f
                // Enhanced perspective with closer camera
                cameraDistance = 12f * density
                // Add subtle shadow offset for depth
                shadowElevation = 8f
                // Slight translation for layering effect
                translationY = 2f
            }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Color(0xFF1F1F1F),
                ambientColor = Color(0xFF121212)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Create texture pattern (subtle noise)
                    val brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x08000000),
                            Color(0x12000000),
                            Color(0x08000000),
                        ),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                        tileMode = TileMode.Clamp
                    )
                    drawRect(brush = brush)

                    // Add a subtle shine/highlight effect
                    val highlightBrush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.0f),
                        ),
                        center = androidx.compose.ui.geometry.Offset(size.width * 0.3f, size.height * 0.3f),
                        radius = size.width * 0.8f
                    )
                    drawRect(brush = highlightBrush)

                    // Add subtle noise-like texture (small dots pattern)
                    val dotSpacing = 20f
                    val dotRadius = 1.5f
                    val dotColor = Color.White.copy(alpha = 0.04f)

                    for (x in 0..(size.width.toInt() / dotSpacing.toInt())) {
                        for (y in 0..(size.height.toInt() / dotSpacing.toInt())) {
                            drawCircle(
                                color = dotColor,
                                radius = dotRadius,
                                center = androidx.compose.ui.geometry.Offset(
                                    x * dotSpacing + (y % 2) * (dotSpacing / 2),
                                    y * dotSpacing
                                )
                            )
                        }
                    }
                },
        ) {
            content()
        }
    }
}
