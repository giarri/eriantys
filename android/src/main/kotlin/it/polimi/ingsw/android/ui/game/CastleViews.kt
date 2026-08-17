package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import it.polimi.ingsw.communication.modelData.CastleData
import it.polimi.ingsw.server.model.baseLogic.StudentColor

private const val TOTAL_TOWERS_3_PLAYER = 6
private const val TOTAL_TOWERS_DEFAULT = 8

private fun totalTowersFor(nPlayer: Int) = if (nPlayer == 3) TOTAL_TOWERS_3_PLAYER else TOTAL_TOWERS_DEFAULT

@Composable
fun MyCastleView(
    castle: CastleData,
    nPlayer: Int,
    selectedIndices: Set<Int>,
    onStudentClick: (Int) -> Unit,
    onConfirmMoveToDiningRoom: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(12.dp)) {
        Text("My castle (${castle.username()})", style = MaterialTheme.typography.titleMedium)

        Text("Waiting room", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            castle.waitingRoom().forEachIndexed { index, color ->
                StudentDot(color, selected = index in selectedIndices, onClick = { onStudentClick(index) })
            }
        }
        Button(onClick = onConfirmMoveToDiningRoom, enabled = selectedIndices.isNotEmpty(), modifier = Modifier.padding(top = 8.dp)) {
            Text("Move to dining room")
        }

        Text("Dining room", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            castle.diningRoom().forEach { (color, count) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StudentDot(color, selected = false, onClick = {})
                    Text(" $count")
                }
            }
        }

        Text(
            "Towers left: ${totalTowersFor(nPlayer) - castle.nTower()}  •  Coins: ${castle.coins()}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp),
        )
        Text(
            castle.lastPlayedCard()?.let { "Last played card: $it" } ?: "No card played yet",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
fun OtherCastlesRow(
    castles: List<CastleData>,
    nPlayer: Int,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(12.dp)) {
        items(castles) { castle ->
            OtherCastleCard(castle, nPlayer)
        }
    }
}

@Composable
private fun OtherCastleCard(castle: CastleData, nPlayer: Int) {
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = castle.towerColor().toComposeColor().copy(alpha = 0.15f)),
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(castle.username(), style = MaterialTheme.typography.labelLarge)
            Text("Towers left: ${totalTowersFor(nPlayer) - castle.nTower()}", style = MaterialTheme.typography.labelSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                castle.diningRoom().forEach { (color, count) ->
                    if (count > 0) Text("${color.name.take(1)}:$count", style = MaterialTheme.typography.labelSmall)
                }
            }
            Text(
                castle.lastPlayedCard()?.let { "Last: $it" } ?: "No card yet",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun StudentDot(color: StudentColor, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(color.toComposeColor())
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.35f)),
            )
        }
    }
}
