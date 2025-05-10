package dimstyl.pointsofinterest.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dimstyl.pointsofinterest.ui.models.PointOfInterestUiModel
import dimstyl.pointsofinterest.ui.theme.BottomBarColor
import dimstyl.pointsofinterest.ui.theme.CardCategoryColor
import dimstyl.pointsofinterest.ui.theme.CardClickToSeeMoreColor
import dimstyl.pointsofinterest.ui.theme.CardContainerColor
import dimstyl.pointsofinterest.ui.theme.CardRatingIconColor
import dimstyl.pointsofinterest.ui.theme.CardTitleColor

@Composable
fun Card(
    pointOfInterestUiModel: PointOfInterestUiModel,
    onClick: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .heightIn(min = 150.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardContainerColor),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.linearGradient(listOf(CardRatingIconColor, BottomBarColor))
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(4.dp),
                text = pointOfInterestUiModel.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = CardTitleColor
            )
            Text(
                modifier = Modifier.padding(8.dp),
                text = pointOfInterestUiModel.category,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = CardCategoryColor
            )
            Row {
                (1..pointOfInterestUiModel.rating.toInt()).forEach { _ ->
                    Icon(
                        modifier = Modifier.padding(vertical = 4.dp),
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = CardRatingIconColor
                    )
                }
            }
            Text(
                modifier = Modifier.padding(8.dp),
                text = "Click to see more",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodySmall,
                color = CardClickToSeeMoreColor
            )
        }
    }
}