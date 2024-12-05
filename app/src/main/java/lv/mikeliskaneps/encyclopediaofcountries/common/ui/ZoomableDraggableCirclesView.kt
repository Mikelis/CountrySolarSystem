package lv.mikeliskaneps.encyclopediaofcountries.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lv.mikeliskaneps.encyclopediaofcountries.R
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponseItem
import lv.mikeliskaneps.encyclopediaofcountries.ui.theme.gold
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.CountryViewModel
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ZoomableCircleGraphView(
    modifier: Modifier = Modifier,
    viewModel: CountryViewModel,
    country: CountriesResponseItem
) {
    var scale by remember { mutableFloatStateOf(0.7f) }
    var offset by remember { mutableStateOf(Offset(0f, 0f)) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val borderCountriesRadius = (660 * (1 + (country.borders?.size ?: 1) / 10)).dp
    val languageCountriesRadius = (960 * (1 + (country.borders?.size ?: 1) / 10)).dp
    val countryInfoList = viewModel.getCountriesInfoList(LocalContext.current, country)
    val favorites by viewModel.favoritesLiveData.observeAsState()


    Box {
        Box(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        scale = scale.coerceIn(0.1f, 3f)
                        offset = Offset(offset.x + pan.x, offset.y + pan.y)
                    }
                }
        ) {

            showCompanionCircles(
                country.languages?.map { it.iso6391 },
                languageCountriesRadius,
                scale,
                offset,
                gold
            ) {
                viewModel.goToDetailsByLanguageCode(it)
            }
            showCompanionCircles(
                country.borders,
                borderCountriesRadius,
                scale,
                offset,
                secondaryColor
            ) {
                viewModel.goToDetailsByCountryCode(it)
            }

            Box(
                modifier = Modifier
                    .size(360.dp * scale)
                    .offset { IntOffset(offset.x.toInt(), offset.y.toInt()) }
                    .graphicsLayer(
                        scaleX = scale, scaleY = scale, shape = CircleShape,
                        clip = true
                    )
                    .align(Alignment.Center)
                    .background(primaryColor)
            ) {
                Column(
                    modifier = modifier
                        .align(Alignment.Center)
                        .padding(6.dp)
                ) {
                    countryInfoList.forEach {
                        Text(
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            text = it,
                            fontSize = 10.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(6.dp)
                        )
                    }
                }

            }
        }

        Box(modifier = modifier.background(Color(0x66FFFFFF))) {
            Row {
                Column(
                    modifier = modifier
                        .padding(16.dp)
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.selected_item), color = primaryColor)
                    Text(stringResource(R.string.border_countries), color = secondaryColor)
                    Text(stringResource(R.string.languages), color = gold)
                }

                FilledIconButton(
                    modifier = Modifier
                        .size(44.dp, 36.dp)
                        .align(Alignment.CenterVertically),
                    onClick = {
                        viewModel.toggleFavorite(country)
                    },

                    shape = RoundedCornerShape(bottomEnd = 8.dp, topStart = 8.dp),
                ) {
                    Image(
                        imageVector = if (favorites?.contains(country) == true) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                        contentDescription = "favorite"
                    )
                }

            }
        }
    }
}

@Composable
private fun BoxScope.showCompanionCircles(
    list: List<String>?,
    borderCountriesRadius: Dp,
    scale: Float,
    offset: Offset,
    color: Color,
    callback: (String) -> Unit = {}
) {
    val angleStep = 360f / getAngleDivider(list)
    list?.forEachIndexed { index, item ->
        val radius = borderCountriesRadius.value * scale

        val angle = Math.toRadians((angleStep * index.toFloat()).toDouble())
        val xOffset = (radius * cos(angle)).toFloat()
        val yOffset = (radius * sin(angle)).toFloat()

        Box(
            modifier = Modifier
                .size(80.dp * scale)
                .offset {
                    IntOffset(
                        (offset.x + xOffset)
                            .times(scale)
                            .toInt(),
                        (offset.y + yOffset)
                            .times(scale)
                            .toInt()
                    )
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    shape = CircleShape,
                    clip = true
                )
                .align(Alignment.Center)
                .background(color)
                .clickable {
                    callback.invoke(item)
                }
        ) {
            Text(
                text = item,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

fun getAngleDivider(list: List<String>?): Int {
    if (list.isNullOrEmpty()) {
        return 1
    }
    return list.size
}