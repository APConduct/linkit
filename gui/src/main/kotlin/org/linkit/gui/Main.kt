package org.linkit.gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.linkit.AngleMode
import org.linkit.Calc
import org.linkit.Parser
import org.linkit.PreciseDisplay

// Custom calculator dark theme
private val CalculatorDarkColorScheme =
        darkColorScheme(
                primary = Color(0xFF4FC3F7), // Light blue for primary actions
                secondary = Color(0xFFFFB74D), // Orange for secondary actions
                tertiary = Color(0xFF81C784), // Green for tertiary actions
                background = Color(0xFF121212), // Deep black background
                surface = Color(0xFF1E1E1E), // Dark gray for cards
                surfaceVariant = Color(0xFF2D2D2D), // Lighter gray for display
                error = Color(0xFFEF5350), // Red for clear/error
                onPrimary = Color(0xFF000000), // Black text on primary
                onSecondary = Color(0xFF000000), // Black text on secondary
                onTertiary = Color(0xFF000000), // Black text on tertiary
                onBackground = Color(0xFFE0E0E0), // Light gray text on background
                onSurface = Color(0xFFE0E0E0), // Light gray text on surface
                onSurfaceVariant = Color(0xFFBDBDBD), // Medium gray text
                onError = Color(0xFFFFFFFF), // White text on error
        )

// Custom calculator light theme
private val CalculatorLightColorScheme =
        lightColorScheme(
                primary = Color(0xFF1976D2), // Blue for primary actions
                secondary = Color(0xFFFF9800), // Orange for secondary actions
                tertiary = Color(0xFF4CAF50), // Green for tertiary actions
                background = Color(0xFFFAFAFA), // Light gray background
                surface = Color(0xFFFFFFFF), // White for cards
                surfaceVariant = Color(0xFFF5F5F5), // Very light gray for display
                error = Color(0xFFD32F2F), // Red for clear/error
                onPrimary = Color(0xFFFFFFFF), // White text on primary
                onSecondary = Color(0xFFFFFFFF), // White text on secondary
                onTertiary = Color(0xFFFFFFFF), // White text on tertiary
                onBackground = Color(0xFF212121), // Dark gray text on background
                onSurface = Color(0xFF212121), // Dark gray text on surface
                onSurfaceVariant = Color(0xFF424242), // Medium gray text
                onError = Color(0xFFFFFFFF), // White text on error
        )

fun main() = application {
    var isOpen by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(true) }
    val windowState = remember { WindowState(width = 400.dp, height = 600.dp) }

    if (isOpen) {
        Window(
                onCloseRequest = { isOpen = false },
                title = "🧮 LinkIt Calculator",
                state = windowState,
                resizable = true
        ) { CalculatorApp(isDarkModeExternal = isDarkMode, onDarkModeChange = { isDarkMode = it }) }
    }
}

@Composable
fun CalculatorApp(isDarkModeExternal: Boolean = true, onDarkModeChange: (Boolean) -> Unit = {}) {
    val calc = remember { Calc() }
    val parser = remember { Parser() }
    var displayText by remember { mutableStateOf("0") }
    var currentInput by remember { mutableStateOf(TextFieldValue("")) }
    var angleMode by remember { mutableStateOf(AngleMode.RADIANS) }
    var isDarkMode by remember { mutableStateOf(isDarkModeExternal) }

    // Sync with external state - only update if different to avoid unnecessary recomposition
    LaunchedEffect(isDarkModeExternal) {
        if (isDarkMode != isDarkModeExternal) {
            isDarkMode = isDarkModeExternal
        }
    }

    // Update calculator angle mode when state changes
    LaunchedEffect(angleMode) { calc.setAngleMode(angleMode) }

    MaterialTheme(
            colorScheme = if (isDarkMode) CalculatorDarkColorScheme else CalculatorLightColorScheme
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with title and controls
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                            text = "🧮 LinkIt Calculator",
                            style = MaterialTheme.typography.headlineSmall
                    )

                    Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Dark mode toggle
                        IconButton(
                                onClick = {
                                    val newMode = !isDarkMode
                                    isDarkMode = newMode
                                    onDarkModeChange(newMode)
                                }
                        ) {
                            Text(
                                    text = if (isDarkMode) "🌙" else "☀️",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Angle mode toggle
                        TextButton(
                                onClick = {
                                    angleMode =
                                            if (angleMode == AngleMode.DEGREES) {
                                                AngleMode.RADIANS
                                            } else {
                                                AngleMode.DEGREES
                                            }
                                }
                        ) {
                            Text(
                                    text =
                                            when (angleMode) {
                                                AngleMode.DEGREES -> "DEG"
                                                AngleMode.RADIANS -> "RAD"
                                            },
                                    color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Input area
                CalculatorInput(
                        currentInput = currentInput,
                        modifier = Modifier.height(100.dp),
                        onInputChange = { currentInput = it },
                        onCalculate = {
                            try {
                                if (currentInput.text.isNotEmpty()) {
                                    val expr = parser.parse(currentInput.text)
                                    val result = calc.eval(expr)
                                    displayText =
                                            PreciseDisplay.formatValue(result, calc.getAngleMode())
                                    currentInput = TextFieldValue("")
                                }
                            } catch (e: Exception) {
                                displayText = "Error: ${e.message}"
                                currentInput = TextFieldValue("")
                            }
                        }
                )

                // Result display
                ResultDisplay(displayText = displayText, modifier = Modifier.height(80.dp))

                HorizontalDivider()

                // Button grid
                CalculatorButtonGrid(
                        onInput = { input ->
                            val isFunction =
                                    input in
                                            listOf(
                                                    "sin()",
                                                    "cos()",
                                                    "tan()",
                                                    "sqrt()",
                                                    "ln()",
                                                    "log10()",
                                                    "exp()",
                                                    "fact()",
                                                    "abs()",
                                                    "floor()",
                                                    "ceil()",
                                                    "round()",
                                                    "rads()",
                                                    "degs()"
                                            )

                            if (isFunction) {
                                val newText = currentInput.text + input
                                currentInput =
                                        TextFieldValue(
                                                text = newText,
                                                selection =
                                                        TextRange(
                                                                newText.length - 1
                                                        ) // Position cursor before closing )
                                        )
                            } else {
                                val newText = currentInput.text + input
                                currentInput =
                                        TextFieldValue(
                                                text = newText,
                                                selection = TextRange(newText.length)
                                        )
                            }
                        },
                        onEquals = {
                            try {
                                if (currentInput.text.isNotEmpty()) {
                                    val expr = parser.parse(currentInput.text)
                                    val result = calc.eval(expr)
                                    displayText = PreciseDisplay.formatValue(result, angleMode)
                                    currentInput = TextFieldValue("")
                                }
                            } catch (e: Exception) {
                                displayText = "Error: ${e.message}"
                                currentInput = TextFieldValue("")
                            }
                        },
                        onClear = {
                            currentInput = TextFieldValue("")
                            displayText = "0"
                        },
                        onBackspace = {
                            if (currentInput.text.isNotEmpty()) {
                                val newText = currentInput.text.dropLast(1)
                                currentInput =
                                        TextFieldValue(
                                                text = newText,
                                                selection = TextRange(newText.length)
                                        )
                            }
                        },
                        modifier = Modifier.weight(0.8f)
                )
            }
        }
    }
}

@Composable
fun CalculatorInput(
        currentInput: TextFieldValue,
        modifier: Modifier = Modifier,
        onInputChange: (TextFieldValue) -> Unit = {},
        onCalculate: () -> Unit = {}
) {
    Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center
        ) {
            SmartInputField(
                    value = currentInput,
                    onValueChange = onInputChange,
                    onEnterPressed = onCalculate,
                    modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ResultDisplay(displayText: String, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Box(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                    text = displayText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun CalculatorButtonGrid(
        onInput: (String) -> Unit,
        onEquals: () -> Unit,
        onClear: () -> Unit,
        onBackspace: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        // Row 1: Functions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("sin", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("cos", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("tan", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("sqrt", onInput, modifier = Modifier.weight(1f))
        }

        // Row 2: More functions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("ln", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("log10", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("exp", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("fact", onInput, modifier = Modifier.weight(1f))
        }

        // Row 3: Numbers and operators
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("7", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("8", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("9", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" / ", onInput, modifier = Modifier.weight(1f))
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("4", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("5", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("6", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" * ", onInput, modifier = Modifier.weight(1f))
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("1", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("2", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("3", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" - ", onInput, modifier = Modifier.weight(1f))
        }

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("0", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(".", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("PI", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" + ", onInput, modifier = Modifier.weight(1f))
        }

        // Row 4: Special functions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CalculatorButton("(", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(")", onInput, modifier = Modifier.weight(1f))
            CalculatorButton("^", onInput, modifier = Modifier.weight(1f))
            CalculatorButton(" % ", onInput, modifier = Modifier.weight(1f))
        }

        // Row 5: Control buttons
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                            )
            ) { Text("Clear") }

            Button(
                    onClick = onBackspace,
                    modifier = Modifier.weight(1f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                            )
            ) { Text("⌫") }

            Button(
                    onClick = onEquals,
                    modifier = Modifier.weight(2f),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                            )
            ) { Text("=", style = MaterialTheme.typography.titleLarge) }
        }
    }
}

@Composable
fun CalculatorButton(text: String, onInput: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
            onClick = {
                val input =
                        if (text in
                                        listOf(
                                                "sin",
                                                "cos",
                                                "tan",
                                                "sqrt",
                                                "ln",
                                                "log10",
                                                "exp",
                                                "fact",
                                                "abs",
                                                "floor",
                                                "ceil",
                                                "round",
                                                "rads",
                                                "degs"
                                        )
                        ) {
                            "$text()"
                        } else {
                            text
                        }
                onInput(input)
            },
            modifier = modifier,
            colors =
                    ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            containerColor = MaterialTheme.colorScheme.surface
                    ),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
    ) { Text(text = text, style = MaterialTheme.typography.bodySmall) }
}

@Composable
fun SmartInputField(
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
        onEnterPressed: () -> Unit,
        modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Smart parentheses completion and cursor positioning
                val processedValue =
                        when {
                            // Function auto-completion with parentheses
                            newValue.text.endsWith("sin") && !value.text.endsWith("sin") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("cos") && !value.text.endsWith("cos") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("tan") && !value.text.endsWith("tan") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("sqrt") && !value.text.endsWith("sqrt") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("ln") && !value.text.endsWith("ln") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("log10") && !value.text.endsWith("log10") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("exp") && !value.text.endsWith("exp") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("fact") && !value.text.endsWith("fact") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("abs") && !value.text.endsWith("abs") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("floor") && !value.text.endsWith("floor") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("ceil") && !value.text.endsWith("ceil") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("round") && !value.text.endsWith("round") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("rads") && !value.text.endsWith("rads") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            newValue.text.endsWith("degs") && !value.text.endsWith("degs") -> {
                                val newText = "${newValue.text}()"
                                TextFieldValue(
                                        text = newText,
                                        selection = TextRange(newText.length - 1)
                                )
                            }
                            // Manual parenthesis completion
                            newValue.text.endsWith("(") &&
                                    !value.text.endsWith("(") &&
                                    newValue.text.length > value.text.length -> {
                                val openCount = newValue.text.count { it == '(' }
                                val closeCount = newValue.text.count { it == ')' }
                                if (openCount > closeCount) {
                                    val newText = "${newValue.text})"
                                    TextFieldValue(
                                            text = newText,
                                            selection = TextRange(newValue.selection.start)
                                    )
                                } else {
                                    newValue
                                }
                            }
                            else -> newValue
                        }
                onValueChange(processedValue)
            },
            modifier =
                    modifier.focusRequester(focusRequester).onKeyEvent { keyEvent ->
                        when {
                            keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp -> {
                                onEnterPressed()
                                true
                            }
                            // Ctrl/Cmd + A to select all
                            keyEvent.key == Key.A &&
                                    (keyEvent.isCtrlPressed || keyEvent.isMetaPressed) &&
                                    keyEvent.type == KeyEventType.KeyUp -> {
                                // Note: Text selection is handled automatically by TextField
                                false
                            }
                            else -> false
                        }
                    },
            label = { Text("Type expression (Enter to calculate)") },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors =
                    OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            cursorColor = MaterialTheme.colorScheme.primary
                    ),
            singleLine = true,
            placeholder = { Text("Type: sin(30), sqrt(16), fact(5), PI/2... (Enter to calculate)") }
    )

    // Auto-focus the text field when the composable is first created
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Preview
@Composable
fun CalculatorAppPreview() {
    CalculatorApp()
}
