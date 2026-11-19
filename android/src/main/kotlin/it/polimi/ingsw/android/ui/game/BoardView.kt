package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.polimi.ingsw.android.R
import it.polimi.ingsw.communication.modelData.CloudData
import it.polimi.ingsw.communication.modelData.IslandData

/** `#islandsPane { -fx-background-color: lightblue; }` — also the backdrop of the card rows. */
val BoardBackdrop = Color(0xFFADD8E6)

// Desktop island/cloud cell: 185x200 with the artwork (220x220 / 180x180) centred on it.
private const val CELL_W = 185f
private const val CELL_H = 200f

/**
 * The desktop's islands pane (GuiDrawer.drawIslands/drawClouds): the first island on the left,
 * the next ones along the top, one on the right, the rest along the bottom (right to left), with
 * the clouds in the middle band between the left and right islands.
 */
@Composable
fun BoardView(
    islands: List<IslandData>,
    clouds: List<CloudData>,
    motherNaturePosition: Int,
    onIslandClick: (Int) -> Unit,
    onCloudClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (islands.isEmpty()) return
    val rightIndex = (islands.size + 1) / 2
    val topIndices = (1 until rightIndex).toList()
    val bottomIndices = (rightIndex + 1 until islands.size).toList().reversed()

    BoxWithConstraints(modifier = modifier.fillMaxWidth().background(BoardBackdrop)) {
        val gutter = 8.dp
        val usable = maxWidth - gutter * 2
        val cellW = minOf(usable / maxOf(topIndices.size, 3), 100.dp)
        val cell = IslandCell(cellW)

        @Composable
        fun island(index: Int) = IslandTile(
            island = islands[index],
            islandNumber = index + 1,
            hasMotherNature = index == motherNaturePosition,
            cell = cell,
            onClick = { onIslandClick(index) },
        )

        Column(modifier = Modifier.padding(gutter), horizontalAlignment = Alignment.CenterHorizontally) {
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                topIndices.forEach { island(it) }
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                island(0)
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    clouds.forEachIndexed { index, cloud ->
                        CloudTile(cloud, cell, onClick = { onCloudClick(index) })
                    }
                }
                if (rightIndex < islands.size) island(rightIndex)
            }
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                bottomIndices.forEach { island(it) }
            }
        }
    }
}

/** Size of one 185x200 board cell and the desktop-pixel -> dp scale that goes with it. */
internal class IslandCell(val width: Dp) {
    val scale = width.value / CELL_W
    val height: Dp = (CELL_H * scale).dp
    fun px(desktopPx: Float): Dp = (desktopPx * scale).dp
}

@Composable
private fun IslandTile(
    island: IslandData,
    islandNumber: Int,
    hasMotherNature: Boolean,
    cell: IslandCell,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(cell.width, cell.height)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        // `.island` background-size is 220x220, i.e. larger than the 185x200 cell it is centred on.
        Image(
            painter = painterResource(if (island.isBlocked()) R.drawable.island_blocked else R.drawable.island_bg),
            contentDescription = "Island $islandNumber",
            modifier = Modifier.requiredSize(cell.px(220f)),
            contentScale = ContentScale.Fit,
        )
        // Inner 145x160 flow pane inset by 20px: students, mother nature, then towers.
        val icon = maxOf(cell.px(25f), 12.dp)
        FlowRow(
            modifier = Modifier.size(cell.px(145f), cell.px(160f)),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
        ) {
            island.students().forEach { (color, count) ->
                repeat(count) { StudentIcon(color, size = icon) }
            }
            if (hasMotherNature) MotherNatureIcon(size = maxOf(cell.px(35f), 14.dp))
            island.ownership()?.let { owner ->
                repeat(island.islandSize()) { TowerIcon(owner, size = maxOf(cell.px(50f), 16.dp)) }
            }
        }
    }
}

@Composable
private fun CloudTile(cloud: CloudData, cell: IslandCell, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(cell.width, cell.height)
            .clickable(enabled = cloud.available()) { onClick() },
        contentAlignment = Alignment.TopStart,
    ) {
        // `.cloud` background-size is 180x180, centred on the cell.
        Image(
            painter = painterResource(R.drawable.cloud_card),
            contentDescription = if (cloud.available()) "Available cloud" else "Taken cloud",
            modifier = Modifier
                .align(Alignment.Center)
                .requiredSize(cell.px(180f)),
            contentScale = ContentScale.Fit,
            alpha = if (cloud.available()) 1f else 0.5f,
        )
        // Students sit at fixed desktop positions: three in a triangle, four in a square.
        val positions = when (cloud.studentList().size) {
            3 -> listOf(60f to 60f, 100f to 87.5f, 60f to 115f)
            4 -> listOf(60f to 60f, 100f to 60f, 60f to 115f, 100f to 115f)
            else -> emptyList()
        }
        cloud.studentList().zip(positions).forEach { (color, pos) ->
            StudentIcon(
                color,
                Modifier.offset(cell.px(pos.first), cell.px(pos.second)),
                size = maxOf(cell.px(25f), 12.dp),
            )
        }
    }
}
