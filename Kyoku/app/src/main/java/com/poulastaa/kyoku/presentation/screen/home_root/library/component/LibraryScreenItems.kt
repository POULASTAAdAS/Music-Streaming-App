package com.poulastaa.kyoku.presentation.screen.home_root.library.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.poulastaa.kyoku.R
import com.poulastaa.kyoku.presentation.screen.home_root.home.component.CustomImageView
import com.poulastaa.kyoku.ui.theme.TestThem
import com.poulastaa.kyoku.ui.theme.background
import com.poulastaa.kyoku.ui.theme.dimens

@Composable
fun LibraryScreenPlaylistListView(
    modifier: Modifier = Modifier,
    isCookie: Boolean,
    authHeader: String,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    shape: Shape = RoundedCornerShape(0.dp),
    name: String,
    imageUrls: List<String>,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(MaterialTheme.shapes.small)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = shape,
            onClick = onClick
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (imageUrls.size < 4) {
                    CustomImageView(
                        isDarkThem = isDarkThem,
                        isCookie = isCookie,
                        headerValue = authHeader,
                        url = imageUrls[0]
                    )
                } else
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(.2f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(.5f)
                        ) {
                            CustomImageView(
                                modifier = Modifier
                                    .fillMaxWidth(.5f)
                                    .fillMaxHeight(),
                                isDarkThem = isDarkThem,
                                isCookie = isCookie,
                                headerValue = authHeader,
                                url = imageUrls[0],
                            )

                            CustomImageView(
                                modifier = Modifier.fillMaxSize(),
                                isDarkThem = isDarkThem,
                                isCookie = isCookie,
                                headerValue = authHeader,
                                url = imageUrls[1],
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            CustomImageView(
                                modifier = Modifier
                                    .fillMaxWidth(.5f)
                                    .fillMaxHeight(),
                                isDarkThem = isDarkThem,
                                isCookie = isCookie,
                                headerValue = authHeader,
                                url = imageUrls[0],
                            )

                            CustomImageView(
                                modifier = Modifier.fillMaxSize(),
                                isDarkThem = isDarkThem,
                                isCookie = isCookie,
                                headerValue = authHeader,
                                url = imageUrls[1],
                            )
                        }
                    }

                Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Medium,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                )
            }
        }
    }
}

@Composable
fun LibraryScreenPlaylistGridView(
    modifier: Modifier = Modifier,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    shape: Shape = MaterialTheme.shapes.small,
    isCookie: Boolean,
    authHeader: String,
    name: String,
    imageUrls: List<String>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            shape = shape,
            onClick = onClick
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                if (imageUrls.size < 4)
                    CustomImageView(
                        modifier = Modifier.fillMaxSize(),
                        isDarkThem = isDarkThem,
                        isCookie = isCookie,
                        headerValue = authHeader,
                        url = imageUrls[0],
                        contentScale = ContentScale.FillBounds
                    )
                else
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(.5f)
                    ) {
                        CustomImageView(
                            modifier = Modifier
                                .fillMaxWidth(.5f)
                                .fillMaxHeight(),
                            isDarkThem = isDarkThem,
                            isCookie = isCookie,
                            headerValue = authHeader,
                            url = imageUrls[0],
                            contentScale = ContentScale.FillBounds
                        )

                        CustomImageView(
                            modifier = Modifier.fillMaxSize(),
                            isDarkThem = isDarkThem,
                            isCookie = isCookie,
                            headerValue = authHeader,
                            url = imageUrls[1],
                            contentScale = ContentScale.FillBounds
                        )
                    }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    CustomImageView(
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .fillMaxHeight(),
                        isDarkThem = isDarkThem,
                        isCookie = isCookie,
                        headerValue = authHeader,
                        url = imageUrls[0],
                        contentScale = ContentScale.FillBounds
                    )

                    CustomImageView(
                        modifier = Modifier.fillMaxSize(),
                        isDarkThem = isDarkThem,
                        isCookie = isCookie,
                        headerValue = authHeader,
                        url = imageUrls[1],
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        Row {
            Text(
                text = name,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
        }
    }
}


@Composable
fun LibraryScreenArtistGridView(
    modifier: Modifier,
    name: String,
    imageUrl: String,
    isCookie: Boolean,
    headerValue: String,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = modifier,
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = background
            )
        ) {
            CustomImageView(
                modifier = Modifier.fillMaxSize(),
                isDarkThem = isDarkThem,
                url = imageUrl,
                isCookie = isCookie,
                headerValue = headerValue,
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}

@Composable
fun LibraryScreenArtistListView(
    modifier: Modifier,
    name: String,
    imageUrl: String,
    isCookie: Boolean,
    headerValue: String,
    isDarkThem: Boolean = isSystemInDarkTheme(),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            modifier = modifier,
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = background
            )
        ) {
            CustomImageView(
                modifier = Modifier.fillMaxSize(),
                isDarkThem = isDarkThem,
                url = imageUrl,
                isCookie = isCookie,
                headerValue = headerValue,
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}


@Composable
fun FavouritePrev(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(
                onClick = onClick
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.favourite),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(MaterialTheme.dimens.small2)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small3))

        Text(
            text = "Favourites",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}


fun LazyGridScope.libraryScreenItemHeading(
    heading: String = "Playlist",
    isGrid: Boolean,
    gridSpan: GridItemSpan = GridItemSpan(if (isGrid) 3 else 1),
    onClick: () -> Unit
) {
    item(
        span = { gridSpan }
    ) {
        Row {
            Row(
                modifier = Modifier.fillMaxWidth(.7f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = heading,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Black,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                LibraryScreenAddButton(
                    onClick = onClick
                )
            }
        }
    }
}


@Composable
fun LibraryScreenItemSeparationLine(
    weight: Float = .97f,
    height: Dp = 1.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(weight)
                .wrapContentHeight()
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    )
            )
        }
    }
}


fun LazyGridScope.headLineSeparator(
    isGrid: Boolean,
    gridSpan: GridItemSpan = GridItemSpan(if (isGrid) 3 else 1)
) {
    item(
        span = { gridSpan }
    ) {
        LibraryScreenItemSeparationLine()
    }

    item(
        span = { gridSpan }
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
    }
}

fun LazyGridScope.largeSpace(
    gridSpan: GridItemSpan
) {
    item(
        span = { gridSpan }
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.large1))
    }
}


@Composable
fun LibraryScreenAddButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
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
        FavouritePrev(
            modifier = Modifier
                .fillMaxWidth(.2f)
                .height(80.dp),
            onClick = {

            }
        )
    }
}