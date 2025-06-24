package com.dinarastepina.decomposedictionary.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import decomposedictionary.shared.generated.resources.Res
import decomposedictionary.shared.generated.resources.allDrawableResources
import decomposedictionary.shared.generated.resources.translate
import org.jetbrains.compose.resources.painterResource

@Composable
fun TopicCard(
    title: String,
    subtitle: String,
    imageRes: String = "",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Image(
                painter = Res.allDrawableResources[imageRes]?.let {
                   painterResource( it )
                } ?: painterResource(Res.drawable.translate),
                contentDescription = title,
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentScale = ContentScale.Crop
            )
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }

            }
        }
    }
} 