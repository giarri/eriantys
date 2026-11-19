package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polimi.ingsw.communication.modelData.TurnData

/**
 * The desktop's turn pane: "Turn Info:", "Turn Phase: ..." and "Player Order: ..." with the
 * current player in a larger font, plus the "use characters" toggle in expert games.
 */
@Composable
fun TurnBanner(
    turn: TurnData,
    expert: Boolean,
    useCharacters: Boolean,
    onUseCharactersChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Turn Info: ", fontSize = 16.sp)
            if (expert) {
                FilterChip(
                    selected = useCharacters,
                    onClick = { onUseCharactersChange(!useCharacters) },
                    label = { Text("use characters") },
                )
            }
        }
        Text("Turn Phase:  ${turn.currentPhase()}", modifier = Modifier.padding(top = 16.dp))
        FlowRow(modifier = Modifier.padding(top = 12.dp)) {
            Text("Player Order:  ")
            turn.actionOrder().forEachIndexed { index, player ->
                if (index != 0) Text(",  ")
                Text(player, fontSize = if (player == turn.currentPlayer()) 16.sp else 14.sp)
            }
        }
    }
}
