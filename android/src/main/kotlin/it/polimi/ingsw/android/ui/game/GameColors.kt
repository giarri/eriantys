package it.polimi.ingsw.android.ui.game

import androidx.compose.ui.graphics.Color
import it.polimi.ingsw.server.model.baseLogic.StudentColor
import it.polimi.ingsw.server.model.baseLogic.Team

/**
 * Placeholder color mapping for game pieces. Phase 5 replaces these with the real game art
 * (the student/teacher/tower PNGs bundled with the desktop client) imported as Android
 * drawables; until then, flat colors keep the interaction logic testable on its own.
 */
fun StudentColor.toComposeColor(): Color = when (this) {
    StudentColor.GREEN -> Color(0xFF4CAF50)
    StudentColor.RED -> Color(0xFFE53935)
    StudentColor.YELLOW -> Color(0xFFFDD835)
    StudentColor.PINK -> Color(0xFFEC407A)
    StudentColor.BLUE -> Color(0xFF42A5F5)
}

fun Team.toComposeColor(): Color = when (this) {
    Team.BLACK -> Color(0xFF212121)
    Team.WHITE -> Color(0xFFEEEEEE)
    Team.GREY -> Color(0xFF9E9E9E)
}
