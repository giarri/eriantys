package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import it.polimi.ingsw.communication.modelData.expertMode.CharacterData
import it.polimi.ingsw.server.model.baseLogic.StudentColor

/**
 * The desktop's `GraphicCharacter` row: 111x200 character art with the card's students laid
 * over it (a 111x100 strip starting 50px down), dimmed while the character is active. The
 * desktop shows the description as a tooltip; here a long-press opens it.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterCardsRow(
    characters: List<CharacterData>,
    activeCharacterName: String?,
    onCharacterClick: (CharacterData) -> Unit,
    modifier: Modifier = Modifier,
) {
    var described by remember { mutableStateOf<CharacterData?>(null) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .background(BoardBackdrop),
        horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(characters) { character ->
            val active = character.name.equals(activeCharacterName, ignoreCase = true)
            Box(
                modifier = Modifier
                    .size(111.dp, 200.dp)
                    .alpha(if (active) 0.5f else 1f)
                    .combinedClickable(
                        onClick = { onCharacterClick(character) },
                        onLongClick = { described = character },
                    ),
            ) {
                Image(
                    painter = painterResource(characterCardRes(character.name)),
                    contentDescription = character.name,
                    modifier = Modifier.size(111.dp, 200.dp),
                    contentScale = ContentScale.FillBounds,
                )
                FlowRow(
                    modifier = Modifier
                        .offset(y = 50.dp)
                        .size(111.dp, 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                ) {
                    character.students.forEach { color -> StudentIcon(color, size = 25.dp) }
                }
            }
        }
    }

    described?.let { character ->
        AlertDialog(
            onDismissRequest = { described = null },
            confirmButton = { TextButton(onClick = { described = null }) { Text("OK") } },
            title = { Text(character.name.lowercase().replaceFirstChar { it.uppercase() }) },
            text = { Text(character.description) },
        )
    }
}

/** Inline swatch shown while a [PendingCharacterInput.AwaitingColors] is in progress. */
@Composable
fun CharacterColorPicker(
    pending: PendingCharacterInput.AwaitingColors,
    onColorPick: (StudentColor) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .background(BoardBackdrop)
        .padding(12.dp)) {
        Text(
            "${pending.charName}: pick ${pending.validCounts.sorted().joinToString(" or ")} student color(s)",
            style = MaterialTheme.typography.labelMedium,
        )
        Row(modifier = Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StudentColor.values().forEach { color ->
                val count = pending.selected.count { it == color }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StudentIcon(color, size = 32.dp, onClick = { onColorPick(color) })
                    if (count > 0) Text("$count", style = MaterialTheme.typography.labelSmall)
                }
            }
            TextButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}
