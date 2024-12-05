package lv.mikeliskaneps.encyclopediaofcountries.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lv.mikeliskaneps.encyclopediaofcountries.R
import lv.mikeliskaneps.encyclopediaofcountries.networking.response.CountriesResponseItem
import lv.mikeliskaneps.encyclopediaofcountries.ui.theme.gold
import lv.mikeliskaneps.encyclopediaofcountries.viewmodel.CountryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier, viewModel: CountryViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredCountries by viewModel.filteredCountriesLiveData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState()
    val favorites by viewModel.favoritesLiveData.observeAsState()

    Box(
        modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),

                    value = query,
                    placeholder = { Text(stringResource(R.string.search)) },
                    onValueChange = {
                        query = it
                        viewModel.filterCountries(it)
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.W400
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 52.dp),
                )

            }
            if (favorites?.isNotEmpty() == true) {
                items(favorites ?: emptyList(), key = { it.name }) { item ->
                    ListRow(query, viewModel, item, Modifier.animateItem(), true)
                }
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
            if (filteredCountries?.isEmpty() == true) {
                item { Text(text = stringResource(R.string.no_results)) }
            } else {

                items(filteredCountries?.filterNot { viewModel.isFavorite(it) }
                    ?: emptyList(), key = { it.name }) { item ->
                    ListRow(query, viewModel, item, Modifier.animateItem())
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }


        }
        if (isLoading == true) {
            CircularProgressIndicator(modifier = modifier.align(alignment = Alignment.Center))
        }
    }
}

@Composable
fun ListRow(
    query: String,
    viewModel: CountryViewModel,
    country: CountriesResponseItem,
    modifier: Modifier,
    isFavorite: Boolean = false
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                viewModel.goToDetails(country)
            },
        colors = CardDefaults.cardColors(containerColor = if (isFavorite) gold else Color.White),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    fontWeight = FontWeight.Medium,
                    text = country.name,
                    fontSize = 24.sp
                )
                if (country.queryMatch != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    QueryBoldText(
                        fullText = country.queryMatch ?: "",
                        boldPart = query
                    )
                }

            }
            FilledIconButton(
                modifier = Modifier.size(44.dp, 36.dp),
                onClick = {
                    viewModel.toggleFavorite(country)
                },

                shape = RoundedCornerShape(bottomEnd = 8.dp, topStart = 8.dp),
            ) {
                Image(
                    imageVector = if (isFavorite) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                    contentDescription = "favorite"
                )
            }
        }
    }
}

@Composable
fun QueryBoldText(fullText: String, boldPart: String) {
    val annotatedString = buildAnnotatedString {
        val startIndex = fullText.indexOf(boldPart)
        if (startIndex != -1) {
            append(fullText.substring(0, startIndex))

            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(boldPart)
            }

            append(fullText.substring(startIndex + boldPart.length))
        } else {
            append(fullText)
        }
    }
    Text(
        text = annotatedString,
        fontSize = 12.sp,
        modifier = Modifier.padding(16.dp)
    )
}