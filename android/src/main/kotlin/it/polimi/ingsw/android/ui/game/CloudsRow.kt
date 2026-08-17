package it.polimi.ingsw.android.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import it.polimi.ingsw.communication.modelData.CloudData

@Composable
fun CloudsRow(
    clouds: List<CloudData>,
    onCloudClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp)) {
        items(clouds.size) { index ->
            CloudTile(clouds[index], onClick = { onCloudClick(index) })
        }
    }
}

@Composable
private fun CloudTile(cloud: CloudData, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = cloud.available()) { onClick() }
            .padding(8.dp),
    ) {
        cloud.studentList().forEach { color ->
            Spacer(
                Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(color.toComposeColor())
                    .padding(2.dp),
            )
            Spacer(Modifier.size(4.dp))
        }
        if (cloud.studentList().isEmpty()) {
            Text(if (cloud.available()) "empty" else "taken")
        }
    }
}
