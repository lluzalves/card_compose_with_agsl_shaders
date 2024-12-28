
----------

### Tutorial Android AGSL — Enhance your Compose Component with shaders.

**Note**: This tutorial uses a shader script from the Android Open Source Project, licensed under the Apache 2.0 License. Adjustments have been made to integrate it with Jetpack Compose. The component is heavily inspired in this attempt tutorial ([https://proandroiddev.com/threads-invitation-card-with-jetpack-compose-2e5b9baede4)](https://proandroiddev.com/threads-invitation-card-with-jetpack-compose-2e5b9baede44)

#### Overview

Jetpack Compose empowers developers to design UI using **Composable** functions, a declarative framework that replaces traditional XML-based layouts. This approach provides flexibility by enabling the reuse of UI components across screens without the need for additional **XML** layouts or manual mappings to Activities, Screens, or Fragments.

By integrating **AGSL** (Android Graphics Shading Language), you can create stunning UI components and elevate user experiences. AGSL shaders are supported in Android 13 and later, making it possible to deliver visually captivating effects for both apps and games. This tutorial will guide you through implementing a Card Flip animation using shaders to create an employee card, showcasing how Compose and AGSL work together for superior visuals.

![](https://cdn-images-1.medium.com/max/1600/1*2kQKCkvKJUvXrhHlm6EdgQ.png)

### Design Phase

To conceptualize the feature, we’ll outline the interactions between the UI and Domain layers. This example uses a clean architecture pattern, focusing solely on these layers for simplicity.

#### Requirements

-   The Card should rotate 360 degrees.
-   It should have distinct front and back covers.
-   Maintain a clean and professional visual appearance.
-   Deliver a user experience comparable to Threads Invite Cards.

#### Architecture Overview

To ensure the separation of concerns, we’ll adopt a unidirectional data flow between the Domain Stack and UI Stack components.

1.  **UI Actions**: The UI sends an intent to the ViewModel.
2.  **ViewModel**: Processes the intent and forwards it to the Domain Layer.
3.  **Domain Layer**: Executes the necessary business logic and returns a result.
4.  **State Updates**: The ViewModel updates the centralized state, which the UI observes and renders.

![](https://cdn-images-1.medium.com/max/1600/1*i-S25J3gWnsb3uepAWzwZg.png)

High Level Diagram for the feature

Based on the diagram, we can now discuss what type of architecture we are aiming for:

### Implementation Steps

#### 1. Create a New Project

Start by creating a new project in Android Studio.

#### 2. Add Dependencies

Update your `libs.version.toml` file with the following dependencies:

#### 3. Update `build.gradle.kts`

At the project level, ensure the Compose plugin is added if not already present:

At the app level, include the Compose and Coil dependencies:

#### 4. Define the Card Model

Create a `CardModel.kt` file to hold the data structure for the card component:

data class Card(  
val id: String,  
val frontText: String,  
val backText: String,  
val username : String,  
val userImage : String,  
val isFlipped: Boolean = false  
)

#### 5. Create Use Cases

In `Domain.kt`, define the use cases for loading and flipping the Card, we will use it in our ViewModel:
```kotlin
class LoadCardUseCase() {  
    operator fun invoke(cardId: String): Card {  
        val card = Card(id = cardId, frontText = "CardFlip", backText = null, username = "Daniel", useImage = "https://avatars.githubusercontent.com/u/8259531?v=4", isFlipped = false)  
        return card.copy(isFlipped = !card.isFlipped)  
    }

class GetFlippedCardUseCase() {  
    operator fun invoke(cardId: String): Card {  
        val card = Card(id = cardId, frontText = null, backText = "Card Flipped !", username = "Daniel", usermage = "https://avatars.githubusercontent.com/u/8259531?v=4", isFlipped = true)  
        return card.copy(isFlipped = card.isFlipped)  
    }  
}
```
#### 6. ViewModel Structure

The `ViewModel` works as the intermediary between the UI and the Domain Layer. It will listen to user intents, process them by invoking the appropriate use cases, and update the UI state accordingly. Maintaining a unidirectional data flow ensures a clear separation of concerns and predictable state management.

#### 6. 1 — Set Up the CardClipState

The `CardFlipSate` interface represents the various states the card flip feature can have. It will use a sealed structure to ensure all possible states are exhaustively handled in the UI.

This includes loading, error, and specific card flip states like: `CardLoading` and `CardFlip`.

By doing so, the UI can react to these states predictably and render appropriate feedback to the user.
```kotlin
sealed class CardFlipIntent{  
    data object Loading : CardFlipIntent()  
    data class LoadCard(val cardId : String) : CardFlipIntent()  
    data object FlipCard : CardFlipIntent(){  
        var cardId = ""  
    }  
}
```
#### 6. 2 — Set Up the CardClipIntent

The `CardFlipIntent` class captures user interactions, such as flipping or loading a card. Each intent corresponds to an action the user can perform, which will be processed `ViewModel`and then updated in the UI state accordingly. This design separates user actions and the underlying state management.

sealed interface CardFlipState  
data object CardLoading : CardFlipState  
data object CardError : CardFlipState {  
var errorMessage: String = "Error"  
}

#### 6. 3 — Set Up the ViewModel

The `CardFlipViewModel` listens for intents from the UI, processes them by invoking appropriate use cases, and updates the application's components state. It maintains a centralized state, allowing the UI to observe and react to state changes.
```kotlin
class CardFlipViewModel(  
    private val flipCardUseCase: GetFlippedCardUseCase,  
    private val loadCardUseCase: LoadCardUseCase  
): ViewModel() {  
    private val _cardFlipState = MutableStateFlow<CardFlipState>(CardLoading)  
    val cardFlipState: StateFlow<CardFlipState> get() = _cardFlipState  
    fun onIntentReceived(intent: CardFlipIntent) {  
        when (intent) {  
            is CardFlipIntent.FlipCard -> {  
                flipCardSide(intent.cardId)  
            }  
            is CardFlipIntent.LoadCard -> {  
                loadCard(intent.cardId)  
            }  
            is CardFlipIntent.Loading ->{  
                _cardFlipState.value = CardLoading  
            }  
        }  
    }  
    private fun flipCardSide(cardId: String) {  
        viewModelScope.launch {  
            _cardFlipState.value = CardLoading  
            try {  
                val card = flipCardUseCase.invoke(cardId)  
                _cardFlipState.value = CardFlip(  
                    isLoading = false,  
                    card = card,  
                    errorMessage = null  
                )  
            }catch (exception : Exception){  
                CardError.errorMessage = exception.localizedMessage ?: "Error"  
                _cardFlipState.value = CardError  
            }  
        }  
    }  
    private fun loadCard(cardId: String) {  
        viewModelScope.launch {  
            _cardFlipState.value = CardLoading  
            try {  
                val card = loadCardUseCase.invoke(cardId)  
                _cardFlipState.value = CardFlip(  
                    isLoading = false,  
                    card = card,  
                    errorMessage = null  
                )  
            }catch (exception : Exception){  
                CardError.errorMessage = exception.localizedMessage ?: "Error"  
                _cardFlipState.value = CardError  
            }  
        }  
    }  
}
```
#### 7.0 — Finally, the IU Components.

Based on the image provided and the requirements, we can think of and define a few compose components we need to work with:

![](https://cdn-images-1.medium.com/max/1600/1*PC_x0TJ5LiDMoDU0JJ8a5w.png)

**CardFlip Front**

![](https://cdn-images-1.medium.com/max/1600/1*xEvU0LYbC5BMIjIffhZLUQ.png)

**CardFlip Back**

IU Key Components:

-   **CardFrontSide**: Displays the front design of the Card, including the user’s image and details.
-   **CardBackSide**: Defines the reverse side of the Card, focusing on branding or additional visuals.
-   **GestureRotationCard**: Handles user gestures to rotate the Card dynamically using horizontal and vertical drags.
-   **InviteCardHolder**: A composable wrapper that applies rotation effects to the Card using Compose’s `graphicsLayer`.

Let’s create them.

#### 7.1 — Card Components

**7.1.1 — Loading**

Add the following code to have a `CircularProgressIndicator` while the content is loaded into the application.
```kotlin
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
            modifier = Modifier.offset(y=40.dp),  
            text = CardLoading.loadingInfo  
        )  
    }  
}
```
![](https://cdn-images-1.medium.com/max/1600/1*azRFpvKA2PQzuRaKaKT0gw.png)

**7.1.1 — CardBrand**

This component displays a brand icon for the Card. It does so by using the `Image` composable to render the image resource set in `painterResource`.
```kotlin
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
```
-   **Key Points**:
-   `modifier`: Allows customization of padding and size.
-   `painterResource`: Loads the drawable resource (`R.drawable.ic_mascot`) for the brand icon.
-   `contentDescription`: Provides accessibility support.

**7.1.2 — CardUserImage**

This code will dynamically load and display the user’s profile image. It uses the Coil library (`ImageLoader`) to fetch the image from a valid URL.
```kotlin
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
```
**Key Points**:

-   `userImage`: URL of the user's profile image.
-   `ImageLoader`: Manages image loading and caching.
-   `ImageRequest.Builder`: Configures the image request.
-   `ImageComponent`: A helper composable to render the fetched image.

**7.1.3 — CardLabel**

The `CardLabel` will add a title and description n in a vertical arrangement.
```kotlin
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
```
**7.1.4 — CardDashDivider**

It adds a visual separator in the Card using dashed lines, leveraging the `Canvas` composable to draw a horizontal dashed line.
```kotlin
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
```
**Key Points**:

-   `Canvas`: Provides a low-level drawing surface.
-   `drawLine`: Draws a horizontal line with specified styling.
-   `PathEffect.dashPathEffect`: Creates a dashed effect with alternating dash and gap lengths.
-   `Modifier.fillMaxWidth`: Ensures the divider spans the Card's width.

----------

With these components, we can construct the `CardFrontSide` and `CardBackSide` with more flexibility. This structure contributes to a modular design approach for the Card, ensuring reusability and adherence to `Compose`declarative principles while allowing developers to create dynamic and customizable UI elements seamlessly.

#### 7.2 — CardFrontSide

It will display the Card’s front design, which includes user details such as the username and profile image; before we do so, we need to create one more composable function to build our Card’s front side:

**7.2.1 — CardContent**

Now, we can construct the content layout for the Card’s front side. To do so, we will combine multiple composables, such as `CardBrand`, `CardUserImage`, `CardLabel`, and `CardDashDivider` to create a structured and visually appealing card layout.
```kotlin
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
```

**Key Points**:

-   **Combines** reusable components like `CardBrand`, `CardUserImage`, `CardLabel`, and `CardDashDivider`.
-   Uses `Column` and `Row` layouts for alignment and spacing.
-   **Ensures** **modularity**, allowing each part to be independently styled or replaced.

After that, we can build our Card’s front successfully.

**CardFrontSide**

We will now put everything together to create the visual structure for the front of the Card, combining top and bottom decorations (`CardHalfCircles`) and the main content (`CardContent`).
```kotlin
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
                .background(customDeepBlue),  
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
```
**Key Points**:

-   Combines reusable components like `CardContent` and `CardHalfCircles`.
-   Uses a `Box` layout to layer components and apply alignment.
-   Applies consistent styling with a background color (`customDeepBlue`).
-   Displays user-specific data such as name, role, and profile image.

This results from the code above and a variation by removing one of the composable components.

![](https://cdn-images-1.medium.com/max/1600/1*ZYvk_Fc9LuagoFLCmD538w.png)

CardFrontFlip Result

![](https://cdn-images-1.medium.com/max/1600/1*ec1vjYOO5QzqEul8XyAY6g.png)

Variation of CardFrontFlip by removing the CardHalfCircles componets.

#### 7.2 — CardBackSide

Now, let’s define the reverse side of the Card. Defines the layout and visuals for the back side of the Card. Based on the design, it combines decorative elements (`CardHalfCircles`) with some branding (logo) and text.
```kotlin
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
                .background(customDeepBlue),  
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
                .background(customDeepBlue),  
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
```

![](https://cdn-images-1.medium.com/max/1600/1*CHv_xoQ1meatzS6-c-JObg.png)

CardBackSide result

We must create a structure that will take hold of both the front and back sides.

We must create one more composable. The `CardHolder` will act as a wrapper composable to hold the front and back sides of the Card.
```kotlin
@Composable  
fun CardHolder(  
    modifier: Modifier = Modifier,  
    frontSide: @Composable () -> Unit = {},  
    backSide: @Composable () -> Unit = {},  
) {  
    Card(  
        modifier = modifier  
    ) {  
        Box(  
            Modifier.fillMaxSize()  
        ) {  
            frontSide()  
        }  
    }  
}

val holder = InviteCardHolder(  
                    frontSide = { CardFrontSide(cardFlip = state) },  
                    backSide = { CardBackSide() },  
                    modifier = modifier  
                )
```                
This implementation provides flexibility by allowing the front and back sides to be passed as composable lambdas and ensures a clean separation between the Card’s structure and its dynamic content.

The result should look like this:

![](https://cdn-images-1.medium.com/max/1600/1*Z6cW5xzxqpZqGKk8mOoADA.gif)

As you can see, there are still missing features, and we can’t rotate the Card to see its backside. To do so, we need to make a few adjustments in the code to support rotation, and then we will finally be able to rotate the Card vertically and horizontally.

#### 7.3 — Gesture And Rotation

To support gesture and rotation, we must create a new composable that handles the user’s gestures and dynamically updates the X and Y rotations. Add the following composable function to your code.
```kotlin
@Composable  
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)  
    fun GestureAndRotationCard(  
        modifier: Modifier = Modifier,  
        state: CardFlipState  
    ) {  
        var gestureAxisX by remember { mutableStateOf(0f) }  
        var gestureAxisY by remember { mutableStateOf(0f) }  
val gestureModifier = Modifier  
            .pointerInput(Unit) {  
                detectDragGestures(  
                    onDrag = { change, dragAmount ->  
                       // Consume drag event  
                        change.consume()   
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
                 // not mapped  
            }  
        }  
    }
````
#### Key Points:

-   **Gesture Handling**:
-   `detectDragGestures` dynamically updates `gestureAxisX` (vertical rotation) and `gestureAxisY` (horizontal rotation) based on user input.
-   Both axes are updated independently, allowing smooth and natural rotation in any direction.
-   **Seamless Integration**:
-   The gesture rotations (`gestureAxisX`, `gestureAxisY`) are passed to `InviteCardHolder`, ensuring dynamic updates.
-   **Front and Back Transitions**:
-   Handles transitions between front and back sides automatically based on the angle.

Now, all we need to do is update the `InviteCardHolder` function to receive the gestures rotation and to make use of it to decide what side of the Card will be presented on the screen. This implementation offers a natural, intuitive rotation experience with proper transitions and no axis-locking conflicts.

```kotlin
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
                        // Correct back-side rotation for Y-axis  
                            rotationY = 180f   
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
                       // Correct back-side rotation for X-axis  
                            rotationX = 180f   
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
```

![](https://cdn-images-1.medium.com/max/1600/0*Kcoe6vD5y1gKW7hW.gif)

Now our card support gesture and reacts to it.

### 8.0 — AGSL (Android Graphics Shading Language) Support in Compose. Finally!

We will combine AGSL with Jetpack Compose to transform our UI components into something more visually captivating beyond static designs.

#### Key Benefits of Using AGSL with Compose:

**Dynamic Effects**:

-   Shaders allow for creating complex visual effects like animations, gradients, and distortions, enhancing user engagement.

**GPU Acceleration**:

-   By leveraging the GPU, AGSL ensures high performance even for demanding visual effects.

**Declarative Integration**:

-   Jetpack Compose’s declarative approach seamlessly integrates with AGSL, enabling modular and reusable shader components.

**Enhanced User Experience**:

-   Custom shader effects can make your UI stand out, offering a polished and professional look.

#### 8.1 — The Solar Flair Shader

We will define a shader script that creates a solar wave effect to do so.

**The Script**

The shader is implemented as a Kotlin variable annotated with `@Language("AGSL")` to ensure proper syntax highlighting and editor support.


  ```kotlin    
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
```

### Explanation of the Shader Code

**Uniform Variables**:

-   `resolution`: Provides the screen resolution.
-   `time`: Tracks the elapsed time, creating a dynamic animation effect.
-   `baseColor` and `backgroundColor`: Define the primary and background colors for blending.

**Dynamic Animation**:

-   The shader uses the `time` variable to create smooth, animated color waves.

**Iteration Loop**:

-   The loop generates iterative adjustments to the UV coordinates (`uvCalc`) for creating complex wave patterns.

**Color Blending**:

-   Combines the `baseColor` and `backgroundColor` using a fading curve to create the wave effect.

**Clamping**:

-   Ensures all color values remain within valid ranges (`0.0 to 1.0`) for proper rendering.

#### 8.2 — Integration with Jetpack Compose

Our integration code will make use of the `RuntimeShader` API with AGSL to render a dynamic solar wave effect as a background in the Compose component.

#### 1. Shader Modifier

The `solarFlareShaderBackground` function applies the shader effect or a fallback gradient, depending on the Android version.

```kotlin
fun Modifier.solarFlareShaderBackground(  
    baseColor: Color,  
    backgroundColor: Color,  
): Modifier =  
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  
        this.then(SolarFlareShaderBackgroundElement(baseColor, backgroundColor))  
    } else {  
        this.then(Modifier.simpleGradient()) // Fallback for older Android versions  
    }
    
```

#### Explanation:

#### Fallback Handling:

-   For Android versions below 13 (TIRAMISU), a simple vertical gradient (`simpleGradient()`) is applied as a fallback.

**Dynamic Application**:

-   Uses `SolarFlareShaderBackgroundElement` for devices supporting AGSL.

#### 2. Solar Flare Shader Element

The `SolarFlareShaderBackgroundElement` connects the shader to the Compose modifier system.
```kotlin
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
```
#### Explanation:

-   `**create**` **and** `**update**`:
-   `create` initializes the node (`SolarFlairShaderBackgroundNode`).
-   `update` updates the shader colors dynamically if the colors change.

3. Shader Node Implementation

The `SolarFlairShaderBackgroundNode` contains the core logic for rendering the shader.

```kotlin
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
            "baseColor",  
            android.graphics.Color.valueOf(  
                baseColor.red,  
                baseColor.green,  
                baseColor.blue,  
                baseColor.alpha  
            )  
        )  
        shader.setColorUniform(  
            "backgroundColor",  
            android.graphics.Color.valueOf(  
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
                    time.floatValue = it / 3000f // Updates time for dynamic animation  
                }  
            }  
        }  
    }  
}
```

#### Explanation:

-   **Dynamic Time Update**:
-   Updates the `time` variable every 150ms to create a smooth animation effect.
-   **Shader Parameters**:
-   `resolution`: Adjust the shader to the device's screen size.
-   `baseColor` and `backgroundColor`: Passed as uniform variables for dynamic color blending.
-   `time`: Drives the animation effect, ensuring smooth transitions over time.

----------

### 4. Fallback Gradient

The `simpleGradient` function applies a static vertical gradient for devices without AGSL support.
```kotlin
fun Modifier.simpleGradient(): Modifier =  
    drawWithCache {  
     val gradientBrush = Brush.verticalGradient(listOf(Blue, PurpleGrey40, customDeepBlue))  
        onDrawBehind {  
            drawRect(gradientBrush, alpha = 1f)  
        }  
    } 
```
#### Explanation:

**Static Gradient**:

-   Displays a static gradient with three colors (`Blue`, `Purple`, `customBlue`).

**Compatibility**:

-   Ensures older devices can display a visually appealing effect.

### Update the Compose Code to make use of it.

We remove the `background`and we make use of the new extension function `solarFlareShaderBackground` in the `Box` component of our Card.
```kotlin
.solarFlareShaderBackground(customLightBlue, customLightBlue)
````

```kotlin
@RequiresApi(Build.VERSION_CODES.TIRAMISU)  
@Composable  
fun CardFrontSide(  
    cardFlip: CardFlip,  
)    Column(  
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
   ```

![](https://cdn-images-1.medium.com/max/1600/1*dgW864nERMvfVym2BOVzKw.png)

CardFlipFront with AGSL Shader applied to it.

We do the same for the `CardFlipBack` updating the code to use `solarFlareShaderBackground` instead of `background` .

```kotlin
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
         ...  
        }  
        Box(  
            modifier = Modifier  
                .fillMaxWidth()  
                .height(400.dp)  
                .solarFlareShaderBackground(customLightBlue, Color.White),  
            contentAlignment = Alignment.TopCenter  
        )  
       .... 
```
![](https://cdn-images-1.medium.com/max/1600/1*NSMOiITaPDZN5ne6xn068A.png)

If the device runs an Android version below the Tiramisu SDK, we will apply a simple gradient effect as our fallback option.

![](https://cdn-images-1.medium.com/max/1600/1*81ATNgkoyxWv4aYz7MMXpA.png)

### Final Result.

![](https://cdn-images-1.medium.com/max/1600/0*gAJidVhDMYfQA-nx.gif)

![](https://cdn-images-1.medium.com/max/1600/0*EEyRrcf__0KRekto.gif)

**Thanks for checking this article!**

**See you soon!**_
