package com.poulastaa.play.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.poulastaa.core.presentation.designsystem.AppThem
import com.poulastaa.core.presentation.designsystem.MoveIcon
import com.poulastaa.core.presentation.designsystem.ThreeDotIcon
import com.poulastaa.core.presentation.designsystem.dimens
import com.poulastaa.core.presentation.ui.imageReqSongCover
import com.poulastaa.core.presentation.ui.model.ViewUiSong
import com.poulastaa.play.domain.ViewSongOperation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongDetailsMovableCard(
    modifier: Modifier = Modifier,
    header: String,
    list: List<ViewSongOperation>,
    song: ViewUiSong,
    onMove: () -> Unit,
    onThreeDotOpenClick: () -> Unit,
    onThreeDotOperationClick: (ViewSongOperation) -> Unit,
    onThreeDotClose: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .fillMaxWidth()
            .padding(MaterialTheme.dimens.small3)
            .height(80.dp),
    ) {
        Icon(
            imageVector = MoveIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onBackground.copy(.8f),
            modifier = Modifier
                .fillMaxHeight()
                .combinedClickable(
                    interactionSource = null,
                    indication = null,
                    onClick = {},
                    onLongClick = onMove
                )
        )

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))

        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.aspectRatio(1f),
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                AsyncImage(
                    model = imageReqSongCover(
                        header = header,
                        url = song.coverImage
                    ),
                    modifier = Modifier
                        .aspectRatio(1f),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(.8f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = song.name,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    color = MaterialTheme.colorScheme.onBackground.copy(.7f),
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = onThreeDotOpenClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = ThreeDotIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(.6f)
                    )
                }

                DropdownMenu(
                    expanded = song.isExpanded,
                    onDismissRequest = onThreeDotClose,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(horizontal = MaterialTheme.dimens.small2)
                ) {
                    list.forEach {
                        DropdownMenuItem(
                            text = { Text(text = it.value) },
                            onClick = { onThreeDotOperationClick(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SongDetailsCard(
    modifier: Modifier = Modifier,
    header: String,
    song: ViewUiSong,
    list: List<ViewSongOperation>,
    onThreeDotOpenClick: () -> Unit,
    onThreeDotOperationClick: (ViewSongOperation) -> Unit,
    onThreeDotClose: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.small3)
                .height(80.dp),
        ) {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier.aspectRatio(1f),
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    AsyncImage(
                        model = imageReqSongCover(
                            header = header,
                            url = song.coverImage
                        ),
                        modifier = Modifier
                            .aspectRatio(1f),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(.8f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = song.name,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        color = MaterialTheme.colorScheme.onBackground.copy(.7f),
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    IconButton(onClick = onThreeDotOpenClick) {
                        Icon(
                            imageVector = ThreeDotIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(.6f)
                        )
                    }

                    DropdownMenu(
                        expanded = song.isExpanded,
                        onDismissRequest = onThreeDotClose,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = MaterialTheme.dimens.small2)
                    ) {
                        list.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it.value) },
                                onClick = { onThreeDotOperationClick(it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppThem {
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            SongDetailsMovableCard(
                song = ViewUiSong(
                    name = "That cool song",
                    artist = "That cool artist",
                    isExpanded = false
                ),
                list = listOf(
                    ViewSongOperation.PLAY_NEXT,
                    ViewSongOperation.PLAY_LAST,
                    ViewSongOperation.ADD_TO_PLAYLIST,
                    ViewSongOperation.ADD_TO_FAVOURITE,
                    ViewSongOperation.VIEW_ARTISTS,
                ),
                onMove = {},
                header = "",
                onThreeDotOpenClick = {},
                onThreeDotOperationClick = {}
            ) {}
        }
    }
}