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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CandidatePage(
    candidates: List<String>,
    candidateComments: List<String> = emptyList(),
    inputText: String,
    onCandidateSelect: (Int) -> Unit,
    backgroundColor: Color,
    textColor: Color,
    hasNextPage: Boolean = false,
    hasPrevPage: Boolean = false,
    onPageDown: (() -> Unit)? = null,
    onPageUp: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    bottomPaddingDp: Int = 0,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = textColor == Color(0xFFE8EAED)
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        // 导航区
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = if (isLandscape) 50.dp else 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // 翻页按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.dp, LocalKeyBorderColor.current, CircleShape)
                        .clip(CircleShape)
                        .background(
                            if (hasPrevPage && onPageUp != null) LocalKeyBackgroundColor.current.copy(alpha = 0.7f)
                            else LocalKeyBackgroundColor.current
                        )
                        .clickable(
                            enabled = hasPrevPage && onPageUp != null,
                            onClick = { onPageUp?.invoke() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "上一页",
                        tint = if (hasPrevPage) textColor else textColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(1.dp, LocalKeyBorderColor.current, CircleShape)
                        .clip(CircleShape)
                        .background(
                            if (hasNextPage && onPageDown != null) LocalKeyBackgroundColor.current.copy(alpha = 0.7f)
                            else LocalKeyBackgroundColor.current
                        )
                        .clickable(
                            enabled = hasNextPage && onPageDown != null,
                            onClick = { onPageDown?.invoke() }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "下一页",
                        tint = if (hasNextPage) textColor else textColor.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(1.dp, LocalKeyBorderColor.current, CircleShape)
                    .clip(CircleShape)
                    .background(LocalKeyBackgroundColor.current)
                    .clickable { onBack?.invoke() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "返回",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            if (candidates.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    candidates.forEachIndexed { index, candidate ->
                        CandidatePageItem(
                            text = candidate,
                            comment = candidateComments.getOrElse(index) { "" },
                            onClick = { onCandidateSelect(index) },
                            textColor = textColor
                        )
                    }
                }
            }
        }

        // 底部留空
        Spacer(
            modifier = Modifier.height(
                if (isLandscape) 15.dp else maxOf(
                    bottomPaddingDp,
                    40
                ).dp
            )
        )
    }
}

@Composable
fun CandidatePageItem(
    text: String,
    comment: String = "",
    onClick: () -> Unit,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val displayComment = comment.replace("~", "")

    Row(

        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
//            .background(textColor.copy(alpha = 0.1f))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier
                .padding(horizontal = 2.dp)
        )
        if (displayComment.isNotEmpty()) {
            Text(
                text = displayComment,
                color = textColor.copy(alpha = 0.5f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 1.dp)
            )
        }
    }
}