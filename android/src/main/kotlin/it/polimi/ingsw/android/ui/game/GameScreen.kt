package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import it.polimi.ingsw.android.R
import it.polimi.ingsw.communication.modelData.BoardData

private const val MAX_CASTLE_WIDTH_DP = 330

/** Same two tabs as the desktop client's game window: "Game" (board) and "Castle" (every castle). */
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val boardData = uiState.boardData
    var tab by rememberSaveable { mutableStateOf(0) }
    var useCharacters by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (boardData == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = tab) {
                    Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Game") })
                    Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Castle") })
                }
                if (tab == 0) {
                    GameTab(boardData, uiState, viewModel, useCharacters, onUseCharactersChange = { useCharacters = it })
                } else {
                    CastleTab(boardData)
                }
            }
        }

        uiState.lastError?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = { TextButton(onClick = viewModel::clearError) { Text("Dismiss") } },
            ) {
                Text(error)
            }
        }
    }
}

@Composable
private fun GameTab(
    boardData: BoardData,
    uiState: GameUiState,
    viewModel: GameViewModel,
    useCharacters: Boolean,
    onUseCharactersChange: (Boolean) -> Unit,
) {
    val expert = boardData.characters().isNotEmpty()
    val pending = uiState.pendingCharacterInput

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        TurnBanner(boardData.turn(), expert, useCharacters && expert, onUseCharactersChange)
        BoardView(
            islands = boardData.islandList(),
            clouds = boardData.cloudList(),
            motherNaturePosition = boardData.motherNaturePosition(),
            onIslandClick = viewModel::onIslandClick,
            onCloudClick = viewModel::onCloudClick,
        )
        if (useCharacters && expert) {
            CharacterCardsRow(
                characters = boardData.characters(),
                activeCharacterName = boardData.activeChar()?.name,
                onCharacterClick = viewModel::onCharacterClick,
            )
            when (pending) {
                is PendingCharacterInput.AwaitingColors -> CharacterColorPicker(
                    pending,
                    onColorPick = viewModel::onCharacterColorPick,
                    onCancel = viewModel::onCancelCharacterInput,
                )
                is PendingCharacterInput.AwaitingIsland -> Row(modifier = Modifier.padding(12.dp)) {
                    Text("${pending.charName}: tap an island")
                    TextButton(onClick = viewModel::onCancelCharacterInput) { Text("Cancel") }
                }
                null -> Unit
            }
        } else {
            AssistantCardHand(boardData.myCastle().deck(), onCardClick = viewModel::onCardClick)
        }
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            CastlePanel(
                castle = boardData.myCastle(),
                nPlayer = boardData.nPlayer(),
                width = minOf(maxWidth, MAX_CASTLE_WIDTH_DP.dp),
                modifier = Modifier.align(Alignment.Center),
                selectedIndices = uiState.selectedWaitingRoomIndices,
                onStudentClick = viewModel::onWaitingRoomStudentClick,
                onDiningRoomClick = viewModel::onConfirmMoveToDiningRoom,
            )
        }
    }
}

/** The desktop's "Castle" tab: every player's castle side by side on the castle backdrop. */
@Composable
private fun CastleTab(boardData: BoardData) {
    val expert = boardData.characters().isNotEmpty()
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.castle_tab_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(50.dp),
        ) {
            (listOf(boardData.myCastle()) + boardData.otherCastles()).forEach { castle ->
                PlayerCastle(castle, boardData.nPlayer(), expert)
            }
        }
    }
}
