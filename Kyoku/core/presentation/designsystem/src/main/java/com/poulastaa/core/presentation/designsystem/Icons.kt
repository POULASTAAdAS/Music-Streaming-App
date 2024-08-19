package com.poulastaa.core.presentation.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource

val AppLogo: Painter
    @Composable
    get() = if (!isSystemInDarkTheme()) painterResource(id = R.drawable.ic_app_logo_night)
    else painterResource(id = R.drawable.ic_app_logo_light)

val GoogleIcon: Painter
    @Composable
    get() = painterResource(id = R.drawable.ic_google)

val EmailIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_email)

val CheckIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_check)

val PasswordIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_password)

val EyeOpenIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_visibility_on)

val EyeCloseIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_visibility_off)

val UserIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_user)

val ArrowBackIcon: ImageVector
    @Composable
    get() = Icons.AutoMirrored.Rounded.ArrowBack

val ArrowDownIcon: ImageVector
    @Composable
    get() = Icons.Rounded.KeyboardArrowDown

val LinkIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_link)

val MusicImage: Painter
    @Composable
    get() = painterResource(id = R.drawable.ic_music)

val DropDownIcon: ImageVector
    @Composable
    get() = Icons.Rounded.ArrowDropDown

val CalenderIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_b_date)

val WarningIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Warning

val SearchIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Search

val SettingsIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Settings


val LogoutIcon: ImageVector
    @Composable
    get() = Icons.AutoMirrored.Rounded.ExitToApp

val HomeSelectedIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_home)

val HomeUnSelectedIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_home_empty)

val LibrarySelectedIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_library)

val LibraryUnSelectedIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_library_empty)

val ThreeLineIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_three_line)

val MoreFromArtistIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_more_from_artist)

val FilterPlaylistIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_filter_playlist)

val FilterArtistIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_filter_artist)

val FilterAlbumIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_filter_album)

val SortTypeListIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_list)

val SortTypeGridIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_grid)

val AddIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Add

val PlayIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_play)


val AddAsPlaylistIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_add_to_playlist)

val DownloadIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_download)

val FollowArtistIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_follow_artist)

val UnFollowArtistIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_unfollow_artist)

val CancelIcon: ImageVector
    @Composable
    get() = Icons.Rounded.Close

val PlayNextIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_play_next_in_queue)

val PlayLastIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_play_last_in_queue)

val FavouriteIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_favourite)

val NotFavouriteIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_remove_favourite)

val PinIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_pin)

val UnPinIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_un_pin)

val ViewIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_view)

val ThreeDotIcon: ImageVector
    @Composable
    get() = Icons.Rounded.MoreVert

val ShuffleIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_shuffle)

val MoveIcon: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.ic_move)
