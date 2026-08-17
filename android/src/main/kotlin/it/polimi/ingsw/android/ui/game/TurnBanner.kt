package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.polimi.ingsw.communication.modelData.TurnData
import it.polimi.ingsw.server.model.baseLogic.TurnPhase

private val PHASE_CYCLE = listOf(TurnPhase.PLANNING, TurnPhase.STUDENTS, TurnPhase.MOTHERNATURE, TurnPhase.CLOUD)

private fun TurnPhase.displayName(): String = when (this) {
    TurnPhase.PLANNING -> "Planning"
    TurnPhase.STUDENTS -> "Move Students"
    TurnPhase.MOTHERNATURE -> "Mother Nature"
    TurnPhase.CLOUD -> "Clouds"
}

/** A "div" at the top of the board showing the four turn phases cycling, with the active one highlighted. */
@Composable
fun PhaseCycle(currentPhase: TurnPhase, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PHASE_CYCLE.forEachIndexed { index, phase ->
                PhaseChip(phase.displayName(), active = phase == currentPhase)
                if (index != PHASE_CYCLE.lastIndex) {
                    Text("→", modifier = Modifier.padding(horizontal = 2.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Text("↻", modifier = Modifier.padding(horizontal = 2.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun PhaseChip(label: String, active: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
fun TurnBanner(turn: TurnData, modifier: Modifier = Modifier) {
    Row(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp)) {
        turn.actionOrder().forEachIndexed { index, player ->
            if (index != 0) Text(", ")
            Text(
                player,
                fontWeight = if (player == turn.currentPlayer()) FontWeight.Bold else FontWeight.Normal,
                color = if (player == turn.currentPlayer()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
