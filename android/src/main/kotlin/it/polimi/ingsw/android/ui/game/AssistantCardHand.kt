package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/** Card id -> turn-order value shown on the card, mirroring GuiDrawer.drawCards's "[i, (i+1)/2]" format. */
private fun turnValueFor(cardId: Int) = (cardId + 1) / 2

@Composable
fun AssistantCardHand(
    deck: List<String>,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val availableCardIds = (1..10).filter { cardId -> "[$cardId, ${turnValueFor(cardId)}]" in deck }

    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(12.dp)) {
        items(availableCardIds) { cardId ->
            AssistantCard(cardId, onClick = { onCardClick(cardId) })
        }
    }
}

@Composable
private fun AssistantCard(cardId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 56.dp, height = 84.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text("$cardId", style = MaterialTheme.typography.titleLarge)
    }
}
