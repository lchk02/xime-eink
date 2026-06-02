package com.kingzcheung.xime.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ToolbarCustomizeView(
    toolbarButtons: List<String>,
    keyTextColor: Color,
    keyBgColor: Color,
    accentColor: Color,
    onUpdateToolbarButtons: ((List<String>) -> Unit)?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allButtons = ToolbarButton.entries
    val originalButtons = remember { toolbarButtons }
    var enabledIds by remember(toolbarButtons) { mutableStateOf(toolbarButtons.toSet()) }

    fun toggleButton(button: ToolbarButton) {
        val newIds = if (button.id in enabledIds) enabledIds - button.id else enabledIds + button.id
        enabledIds = newIds
        onUpdateToolbarButtons?.invoke(
            ToolbarButton.entries.filter { it.id in newIds }.map { it.id }
        )
    }

    val itemsPerPage = 8
    val pages = allButtons.chunked(itemsPerPage).map { page ->
        page + List(itemsPerPage - page.size) { null }
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(keyBgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "点击图标切换，上方候选栏会实时预览效果",
            color = keyTextColor.copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 4
                ) {
                    pages[page].forEach { button ->
                        if (button != null) {
                            val isEnabled = button.id in enabledIds
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isEnabled) accentColor.copy(0.5f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (isEnabled) Color.Transparent
                                            else keyTextColor.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        )
                                        .clickable { toggleButton(button) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = button.icon,
                                        contentDescription = button.label,
                                        tint = if (isEnabled) accentColor else keyTextColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Text(
                                    text = button.label,
                                    fontSize = 11.sp,
                                    color = keyTextColor.copy(alpha = 0.8f),
                                    maxLines = 1
                                )
                            }
                        } else {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }

        if (pages.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) keyTextColor
                                else keyTextColor.copy(alpha = 0.3f)
                            )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                enabledIds = originalButtons.toSet()
                onUpdateToolbarButtons?.invoke(originalButtons)
                onDismiss()
            }) {
                Text("取消", color = keyTextColor)
            }
            TextButton(onClick = onDismiss) {
                Text("确定", color = accentColor)
            }
        }
    }
}
