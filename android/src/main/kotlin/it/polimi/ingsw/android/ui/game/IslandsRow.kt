package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import it.polimi.ingsw.communication.modelData.IslandData

@Composable
fun IslandsRow(
    islands: List<IslandData>,
    motherNaturePosition: Int,
    onIslandClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(12.dp)) {
        items(islands.size) { index ->
            IslandTile(
                island = islands[index],
                islandNumber = index + 1,
                hasMotherNature = index == motherNaturePosition,
                onClick = { onIslandClick(index) },
            )
        }
    }
}

@Composable
private fun IslandTile(island: IslandData, islandNumber: Int, hasMotherNature: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(96.dp)
            .clickable { onClick() },
        border = if (hasMotherNature) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Text("#$islandNumber", style = MaterialTheme.typography.labelMedium)
            if (hasMotherNature) Text("MN", style = MaterialTheme.typography.labelSmall)

            island.students().forEach { (color, count) ->
                if (count > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.layout.Spacer(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(color.toComposeColor()),
                        )
                        Text(" $count", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            island.ownership()?.let { owner ->
                Text(
                    "${island.islandSize()}x tower",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(owner.toComposeColor())
                        .padding(horizontal = 4.dp),
                )
            }
        }
    }
}
