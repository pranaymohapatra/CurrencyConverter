package com.pranay.currencyconverter.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pranay.currencyconverter.presentation.theme.Pink40
import com.pranay.currencyconverter.presentation.viewmodel.CurrencySelectionState
import com.pranay.currencyconverter.presentation.viewmodel.GlobalState
import com.pranay.currencyconverter.presentation.viewmodel.MainViewModel

@Composable
fun CurrencyConverterScreen(
    viewModel: MainViewModel
) {
    val currencySelectionState by viewModel.currencySelectionState.collectAsState()
    val converted by viewModel.convertedCurrency.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Pink40),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Currency Converter",
            fontWeight = FontWeight.SemiBold,
            color = Color.Blue,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        CurrencySelectionSection(
            textFieldValue = currencySelectionState.inputAmount,
            onInputChanged = viewModel::inputChanged,
            onCurrencyChanged = viewModel::setFromCurrency,
            isTextFieldReadOnly = false,
            currencySelectionState = currencySelectionState
        )
        CurrencySelectionSection(
            textFieldValue = converted.convertedValue,
            onCurrencyChanged = viewModel::setToCurrency,
            isTextFieldReadOnly = true,
            currencySelectionState = currencySelectionState
        )
        if (!converted.exchangeRateText.isNullOrEmpty()) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = converted.exchangeRateText!!,
                fontWeight = FontWeight.SemiBold,
                color = Color.Green,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CurrencySelectionSection(
    textFieldValue: String,
    onInputChanged: (String) -> Unit = {},
    onCurrencyChanged: (String) -> Unit,
    isTextFieldReadOnly: Boolean,
    currencySelectionState: CurrencySelectionState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        val selectedCurrency =
            if (isTextFieldReadOnly) currencySelectionState.toCurrency else currencySelectionState.fromCurrency
        var selectedIndex by remember {
            mutableStateOf(currencySelectionState.currencyList.indexOf(selectedCurrency))
        }
        val selectedLambda = remember<(Int, String) -> Unit> {
            { index, item ->
                selectedIndex = index
                onCurrencyChanged(item)
            }

        }
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onInputChanged,
            readOnly = isTextFieldReadOnly,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions { keyboardController?.hide() }
        )
        Spacer(modifier = Modifier.width(4.dp))
        LargeDropdownMenu(
            modifier = Modifier.width(108.dp),
            label = "CURRENCY",
            items = currencySelectionState.currencyList,
            selectedIndex = selectedIndex,
            onItemSelected = selectedLambda
        )
    }
}

@Composable
fun ShowLoaderOrError(globalState: GlobalState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        if (globalState.errorMessage != null)
            Text(
                text = globalState.errorMessage,
                color = Color.Yellow,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        else
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
    }
}