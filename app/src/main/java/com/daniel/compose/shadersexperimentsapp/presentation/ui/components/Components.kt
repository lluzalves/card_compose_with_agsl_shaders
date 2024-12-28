package com.daniel.compose.shadersexperimentsapp.presentation.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import com.daniel.compose.shadersexperimentsapp.R
import com.daniel.compose.shadersexperimentsapp.customDeepBlue
import com.daniel.compose.shadersexperimentsapp.customDeepPurple
import com.daniel.compose.shadersexperimentsapp.customLightBlue
import com.daniel.compose.shadersexperimentsapp.presentation.card.CardError
import com.daniel.compose.shadersexperimentsapp.presentation.card.CardFlip
import com.daniel.compose.shadersexperimentsapp.presentation.card.CardFlipState
import com.daniel.compose.shadersexperimentsapp.presentation.card.CardLoading
import com.daniel.compose.shadersexperimentsapp.presentation.card.NoCard
import java.util.Calendar
import kotlin.math.abs


private val spaceBetweenItems = 28.dp
private val framePadding = 24.dp

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CardFrontSide(
    cardFlip: CardFlip,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .solarFlareShaderBackground(customLightBlue, customLightBlue),
            contentAlignment = Alignment.Center,
        ) {

            // Top black half circle.
            CardHalfCircles(modifier = Modifier.align(alignment = Alignment.TopCenter))


            // Card content.
            CardContent(
                date = Calendar.getInstance().time.toString(),
                userId = cardFlip.card?.id ?: "0",
                username = cardFlip.card?.username ?: "User",
                userImage = cardFlip.card?.userImage ?: ""
            )

            // Bottom black half circle.
            CardHalfCircles(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        }
    }
}

@Composable
fun CardHalfCircles(
    modifier: Modifier
) {
    Canvas(
        modifier = modifier
            .border(color = customDeepPurple, width = 2.dp)
    ) {
        drawCircle(
            color = Color.White,
            radius = 24.dp.toPx()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CardContent(
    date: String,
    userId: String,
    username: String,
    userImage: String,
) {
    Column(
        modifier = Modifier
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            CardBrand(modifier = Modifier.align(alignment = Alignment.Bottom))
            CardUserImage(userImage = userImage)

        }

        Spacer(modifier = Modifier.height(spaceBetweenItems))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            CardLabel(title = "JOINED", info = date)
        }

        Spacer(modifier = Modifier.height(spaceBetweenItems))

        CardLabel(title = "NAME", info = username)

        Spacer(modifier = Modifier.height(spaceBetweenItems))

        CardLabel(title = "ROLE", info = userId)


        Spacer(modifier = Modifier.height(spaceBetweenItems))

        CardDashDivider()

        Spacer(modifier = Modifier.height(spaceBetweenItems))

    }
}

@Composable
fun CardLabel(title: String, info: String) {
    Column {
        Text(
            modifier = Modifier.padding(
                horizontal = framePadding
            ),
            text = title,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
            )
        )

        Text(
            modifier = Modifier.padding(
                horizontal = framePadding
            ),
            text = info,
            color = Color.White,
            style = TextStyle(
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
fun CardBrand(
    modifier: Modifier
) {
    Image(
        modifier = modifier
            .padding(all = 16.dp)
            .size(size = 100.dp),
        painter = painterResource(id = R.drawable.ic_mascot),
        contentDescription = "Card brand icon"
    )
}

@Composable
fun CardDashDivider() {
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.White,
            start = Offset(0f, 0f),
            strokeWidth = 2f,
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 14f), 0f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun CardUserImage(userImage: String) {
    val imageLoader = ImageLoader(LocalContext.current)
    val request = ImageRequest.Builder(LocalContext.current)
        .data(userImage)
        .build()
    imageLoader.enqueue(request)
    ImageComponent(userImage, imageLoader, "")
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ImageComponent(imageUrl: String, imageLoader: ImageLoader, contentDescription: String) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl, imageLoader = imageLoader),
        contentDescription = contentDescription,
        alignment = Alignment.TopCenter,
        modifier = Modifier
            .padding(all = 16.dp)
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
    )
}

@Composable
fun CardBackSide() {
    Column(
        Modifier
            .fillMaxWidth()
            .height(500.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .solarFlareShaderBackground(customDeepBlue, customLightBlue),

            contentAlignment = Alignment.Center,
        ) {
            CardHalfCircles(modifier = Modifier.align(alignment = Alignment.TopCenter))

            Image(
                modifier = Modifier.size(size = 160.dp),
                painter = painterResource(id = R.drawable.ic_mascot),
                contentDescription = ""
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .solarFlareShaderBackground(customLightBlue, Color.White),

            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        horizontal = framePadding,
                    )
                    .align(Alignment.TopCenter),
                text = "CardFlip",
                color = Color.White,
                style = TextStyle(
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            )

            CardHalfCircles(modifier = Modifier.align(alignment = Alignment.BottomCenter))
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CircularProgressIndicator(
            color = customDeepPurple,
            strokeWidth = 4.dp
        )
        Text(
            modifier = Modifier.offset(y = 40.dp),
            text = CardLoading.loadingInfo
        )
    }
}


@Composable
fun InviteCardHolder(
    modifier: Modifier = Modifier,
    positionAxisY: Float,
    positionAxisX: Float,
    frontSide: @Composable () -> Unit = {},
    backSide: @Composable () -> Unit = {},
) {
    val isYAxisBackSide = abs(positionAxisY.toInt()) % 360 in 91..270
    val isXAxisBackSide = abs(positionAxisX.toInt()) % 360 in 91..270
    Card(
        modifier = modifier
            .graphicsLayer {
                rotationX = positionAxisX
                rotationY = positionAxisY
                cameraDistance = 14f * density
            },
    ) {
        when {
            isYAxisBackSide -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f // Correct back-side rotation for Y-axis
                        }
                ) {
                    backSide()
                }
            }

            isXAxisBackSide -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationX = 180f // Correct back-side rotation for X-axis
                        }
                ) {
                    backSide()
                }
            }

            else -> {
                Box(Modifier.fillMaxSize()) {
                    frontSide()
                }
            }
        }
    }
}

    @Composable
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun GestureAndSensorRotationCard(
        modifier: Modifier = Modifier,
        state: CardFlipState
    ) {
        var gestureAxisX by remember { mutableStateOf(0f) }
        var gestureAxisY by remember { mutableStateOf(0f) }

        val gestureModifier = Modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume() // Consume drag event
                        val (dragX, dragY) = dragAmount

                        // Update rotations dynamically
                        gestureAxisY = (gestureAxisY + dragX) % 360 // Y-axis for horizontal rotation
                        gestureAxisX = (gestureAxisX + dragY) % 360 // X-axis for vertical rotation
                    }
                )
            }
        when (state) {
            CardError -> {
            }

            is CardFlip -> {
                InviteCardHolder(
                    frontSide = { CardFrontSide(cardFlip = state) },
                    backSide = { CardBackSide() },
                    positionAxisX = gestureAxisX,
                    positionAxisY = gestureAxisY,
                    modifier = modifier.then(gestureModifier)
                )
            }

            CardLoading -> {
                LoadingScreen()
            }

            NoCard -> {

            }
        }
    }


    @Composable
    @Preview
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun CardFrontSidePreview() {
        CardFrontSide(
            CardFlip(
                false,
                com.daniel.compose.shadersexperimentsapp.domain.model.Card(
                    "1",
                    "Daniel",
                    "Teste",
                    "Daniel",
                    "https://avatars.githubusercontent.com/u/8259531?v=4"
                ),
                null
            )
        )
    }


    @Composable
    @Preview
    fun CardBackSidePreview() {
        CardBackSide()
    }