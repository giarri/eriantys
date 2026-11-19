package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/** Card id -> turn-order value shown on the card, mirroring GuiDrawer.drawCards's "[i, (i+1)/2]" format. */
private fun turnValueFor(cardId: Int) = (cardId + 1) / 2

/** `.assistant`: 111x200 cards on the light-blue `#cardsFlowPane`. */
@Composable
fun AssistantCardHand(
    deck: List<String>,
    onCardClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val availableCardIds = (1..10).filter { cardId -> "[$cardId, ${turnValueFor(cardId)}]" in deck }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(BoardBackdrop),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(availableCardIds) { cardId ->
            Image(
                painter = painterResource(assistantCardRes(cardId)),
                contentDescription = "Assistant card $cardId",
                modifier = Modifier
                    .size(111.dp, 200.dp)
                    .clickable { onCardClick(cardId) },
                contentScale = ContentScale.FillBounds,
            )
        }
    }
}
