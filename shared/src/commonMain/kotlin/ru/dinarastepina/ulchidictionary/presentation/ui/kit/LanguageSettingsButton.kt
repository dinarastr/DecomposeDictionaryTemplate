package ru.dinarastepina.ulchidictionary.presentation.ui.kit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.dinarastepina.ulchidictionary.domain.model.LANGUAGE
import decomposedictionary.shared.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import decomposedictionary.shared.generated.resources.ic_arrow

@Composable
fun LanguageSettingsButton(
    languageOne: LANGUAGE,
    languageTwo: LANGUAGE,
    onClick: (LANGUAGE) -> Unit
) {
    val brush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0ABAB5),
            Color(0xffABA5A5)
        )
    )
    Button(
        modifier = Modifier.background(
            brush = brush,
            shape = ButtonDefaults.shape
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        onClick = {
            onClick(
                languageTwo
            )
        }
    ) {

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = languageOne.value,
                fontSize = 12.sp
            )
            Icon(
                painter = painterResource(Res.drawable.ic_arrow),
                contentDescription = null
            )
            Text(text = languageTwo.value,
                fontSize = 12.sp)
        }
    }
}