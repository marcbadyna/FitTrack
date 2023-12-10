package com.example.fittrack

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fittrack.data.Tab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitTopAppBar(selectedTab: Tab) {
    Column {
        val title = when (selectedTab) {
            Tab.Home -> stringResource(R.string.home)
            Tab.Record -> stringResource(R.string.record)
            Tab.Stats -> stringResource(R.string.stats)
        }

        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            modifier = Modifier
        )

        Divider(
            color = Color.Gray,
            thickness = 0.5.dp,
        )
    }
}

@Composable
fun BottomBarButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

@Composable
fun BottomBar(
    selectedTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    BottomAppBar(
        content = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                BottomBarButton(
                    text = stringResource(R.string.home),
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == Tab.Home
                ) { onTabSelected(Tab.Home) }

                BottomBarButton(
                    text = stringResource(R.string.record),
                    icon = Icons.Default.AddCircle,
                    isSelected = selectedTab == Tab.Record
                ) { onTabSelected(Tab.Record) }

                BottomBarButton(
                    text = stringResource(R.string.stats),
                    icon = Icons.Default.CalendarToday,
                    isSelected = selectedTab == Tab.Stats
                ) { onTabSelected(Tab.Stats) }
            }
        }
    )
}