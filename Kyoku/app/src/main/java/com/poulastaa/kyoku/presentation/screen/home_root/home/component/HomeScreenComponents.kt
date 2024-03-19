package com.poulastaa.kyoku.presentation.screen.home_root.home.component

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.poulastaa.kyoku.R
import com.poulastaa.kyoku.data.model.screens.home.HomeUiArtistPrev
import com.poulastaa.kyoku.ui.theme.TestThem
import com.poulastaa.kyoku.ui.theme.dimens
import com.poulastaa.kyoku.utils.BitmapConverter


fun LazyListScope.homeScreenArtistList(
    artistPrev: List<HomeUiArtistPrev>,
    isSmallPhone: Boolean
) {
    items(artistPrev.size) { artistIndex ->
        Row(
            modifier = Modifier.height(if (isSmallPhone) 60.dp else 70.dp),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
        ) {
            HomeScreenCard(
                size = 60.dp,
                imageUrl = artistPrev[artistIndex].artistCover,
                shape = CircleShape,
                onClick = {

                }
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(
                        onClick = {

                        },
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "More from")
                Text(
                    text = artistPrev[artistIndex].name,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium1)
        ) {
            items(artistPrev[artistIndex].lisOfPrevSong.size) { songIndex ->
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    HomeScreenCard(
                        size = 120.dp,
                        imageUrl = artistPrev[artistIndex]
                            .lisOfPrevSong[songIndex].coverImage,
                    ) {

                    }

                    Text(
                        modifier = Modifier
                            .width(120.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(.8f),
                                shape = RoundedCornerShape(
                                    bottomEnd = MaterialTheme.dimens.small3,
                                    bottomStart = MaterialTheme.dimens.small3
                                )
                            ),
                        text = artistPrev[artistIndex]
                            .lisOfPrevSong[songIndex].title,
                        maxLines = 2,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
            item {
                HomeScreenCardMore {

                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
    }
}


@Composable
fun CustomToast(
    modifier: Modifier = Modifier,
    message: String,
    color: Color = MaterialTheme.colorScheme.primary,
    fontWeight: FontWeight = FontWeight.Medium,
    fontSize: TextUnit = MaterialTheme.typography.titleMedium.fontSize
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message, modifier = Modifier.padding(MaterialTheme.dimens.small3),
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = color
        )
    }
}


@Composable
fun HomeScreenCard(
    modifier: Modifier = Modifier,
    size: Dp,
    elevation: Dp = 10.dp,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    imageUrl: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .size(size),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = onClick
    ) {
        PlaylistImage(
            isDarkThem = isDarkThem,
            url = imageUrl
        )
    }
}

@Composable
fun HomeScreenCardMore(
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp),
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "More",
                fontWeight = FontWeight.Black,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun HomeScreenCardWithText(
    modifier: Modifier,
    name: String,
    imageUrl: String,
    elevation: Dp = 10.dp,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            PlaylistImage(
                isDarkThem = isDarkThem,
                url = imageUrl
            )

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium1))

            Text(
                text = name,
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                maxLines = 2,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun HomeScreenCardPlaylistPrev(
    modifier: Modifier,
    name: String,
    imageUrls: List<String>,
    elevation: Dp = 10.dp,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    shape: CornerBasedShape = MaterialTheme.shapes.small,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1f / 3)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(1f / 2)
                ) {
                    PlaylistImage(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(1f / 2),
                        isDarkThem = isDarkThem,
                        url = imageUrls[0],
                    )

                    if (imageUrls.size >= 2)
                        PlaylistImage(
                            modifier = Modifier
                                .fillMaxSize(),
                            isDarkThem = isDarkThem,
                            url = imageUrls[1],
                        )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    if (imageUrls.size >= 3)
                        PlaylistImage(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(1f / 2),
                            isDarkThem = isDarkThem,
                            url = imageUrls[2],
                        )
                    if (imageUrls.size >= 4)
                        PlaylistImage(
                            modifier = Modifier
                                .fillMaxSize(),
                            isDarkThem = isDarkThem,
                            url = imageUrls[3],
                        )
                }
            }

            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

            Text(
                text = name,
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.titleSmall.fontSize,
                maxLines = 2,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun PlaylistImage(
    modifier: Modifier = Modifier,
    isDarkThem: Boolean,
    url: String,
) {
    BitmapConverter.decodeToBitmap(url).let {
        if (it == null)
            Image(
                modifier = modifier,
                painter = painterResource(
                    id = if (isDarkThem) R.drawable.night_logo
                    else R.drawable.light_logo
                ),
                contentDescription = null
            )
        else Image(
            modifier = modifier,
            bitmap = it,
            contentDescription = null
        )
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview
@Composable
private fun Preview() {
    TestThem {
        HomeScreenCardPlaylistPrev(
            modifier = Modifier
                .height(100.dp)
                .width(240.dp),
            imageUrls = listOf("", "", "", ""),
            name = "Your Favourite"
        ) {

        }
    }
}