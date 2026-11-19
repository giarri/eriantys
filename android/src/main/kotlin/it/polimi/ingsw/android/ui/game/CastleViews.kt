package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polimi.ingsw.android.R
import it.polimi.ingsw.communication.modelData.CastleData
import it.polimi.ingsw.server.model.baseLogic.StudentColor

// Desktop castle geometry (GraphicCastle.java): a 290x675 panel made of a 150px tower garden,
// a 70px professor row, a 347px dining room and a 108px waiting room. Everything below is
// expressed in those desktop pixels and scaled by `width / CASTLE_W` so the panel keeps its look.
private const val CASTLE_W = 290f
private const val CASTLE_H = 675f
private const val PROFESSORS_Y = 150f
private const val DINING_Y = PROFESSORS_Y + 70f
private const val WAITING_Y = DINING_Y + 347f
private const val STUDENT = 25f
private const val COLUMN_STEP = 47.5f

private fun Modifier.placed(x: Float, y: Float, scale: Float) = offset((x * scale).dp, (y * scale).dp)

private fun totalTowersFor(nPlayer: Int) = if (nPlayer == 3) 6 else 8

/**
 * The desktop's `GraphicCastle`: the castle artwork with towers on top, professors below them,
 * the dining-room tables as five columns and the waiting room at the bottom.
 * [onStudentClick] / [onDiningRoomClick] are null for read-only castles (other players').
 */
@Composable
fun CastlePanel(
    castle: CastleData,
    nPlayer: Int,
    width: Dp,
    modifier: Modifier = Modifier,
    selectedIndices: Set<Int> = emptySet(),
    onStudentClick: ((Int) -> Unit)? = null,
    onDiningRoomClick: (() -> Unit)? = null,
) {
    val s = width.value / CASTLE_W
    Box(modifier = modifier.size(width, (CASTLE_H * s).dp)) {
        Image(
            painter = painterResource(R.drawable.castle_bg),
            contentDescription = null,
            modifier = Modifier.size(width, (CASTLE_H * s).dp),
            contentScale = ContentScale.FillBounds,
        )

        // Tower garden: towers already placed on islands, zig-zagging in two rows.
        val towerOffset = if (nPlayer == 3) 75f else 50f
        var towerX = 50f
        var towerY = 20f
        repeat(totalTowersFor(nPlayer) - castle.nTower()) { i ->
            TowerIcon(castle.towerColor(), Modifier.placed(towerX, towerY, s), size = (50f * s).dp)
            if (i % 2 != 0) towerX += towerOffset
            towerY = if (towerY == 20f) 90f else 20f
        }

        // Professors: one slot per color, filled only when this castle owns the teacher.
        StudentColor.values().forEachIndexed { i, color ->
            if (castle.teachers()[color] == castle.towerColor()) {
                TeacherIcon(color, Modifier.placed(32.5f + COLUMN_STEP * i, PROFESSORS_Y + 17f, s), size = (35f * s).dp)
            }
        }

        // Dining room: one column per color, students stacked from the bottom up. Tapping the
        // room moves the selected waiting-room students into it, like clicking it on desktop.
        Box(
            modifier = Modifier
                .placed(0f, DINING_Y, s)
                .size(width, (347f * s).dp)
                .then(if (onDiningRoomClick != null) Modifier.clickable { onDiningRoomClick() } else Modifier),
        )
        StudentColor.values().forEachIndexed { i, color ->
            repeat(castle.diningRoom()[color] ?: 0) { k ->
                StudentIcon(
                    color,
                    Modifier.placed(37f + COLUMN_STEP * i, DINING_Y + 13f + 287f - 32f * k, s),
                    size = (STUDENT * s).dp,
                )
            }
        }

        // Waiting room: first student on the left, then pairs stacked in columns.
        var x = 84.5f
        var y = 16f
        castle.waitingRoom().forEachIndexed { index, color ->
            val (px, py) = if (index == 0) 37f to 16f else x to y
            if (index > 0) {
                if (index % 2 == 0) x += COLUMN_STEP
                y = if (y == 16f) 55f else 16f
            }
            StudentIcon(
                color,
                Modifier.placed(px, WAITING_Y + py, s),
                size = (STUDENT * s).dp,
                selected = index in selectedIndices,
                onClick = onStudentClick?.let { click -> { click(index) } },
            )
        }
    }
}

/** The desktop's `OtherGraphicCastle`: username / coins / last played card on top of the castle. */
@Composable
fun PlayerCastle(
    castle: CastleData,
    nPlayer: Int,
    expert: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.width(CASTLE_W.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(modifier = Modifier.width(100.dp)) {
                Text(castle.username(), fontSize = 20.sp)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                if (expert) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 25.dp)) {
                        Text("${castle.coins()} x")
                        CoinIcon(Modifier.padding(start = 10.dp), size = 50.dp)
                    }
                }
            }
            castle.lastPlayedCard()?.let { card ->
                assistantIdOf(card)?.let { id ->
                    Image(
                        painter = painterResource(assistantCardRes(id)),
                        contentDescription = "Last played card",
                        modifier = Modifier.size(69.375.dp, 125.dp),
                        contentScale = ContentScale.FillBounds,
                    )
                }
            }
        }
        CastlePanel(castle, nPlayer, width = CASTLE_W.dp)
    }
}

/** Inverse of the "[id, turnValue]" card format used by the server (see GuiDrawer.drawCards). */
internal fun assistantIdOf(card: String): Int? =
    (1..10).firstOrNull { card == "[$it, ${(it + 1) / 2}]" }
