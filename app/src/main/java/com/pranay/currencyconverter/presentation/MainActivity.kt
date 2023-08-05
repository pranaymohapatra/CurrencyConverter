package com.pranay.currencyconverter.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pranay.currencyconverter.presentation.composables.CurrencyConverterScreen
import com.pranay.currencyconverter.presentation.composables.ShowLoaderOrError
import com.pranay.currencyconverter.presentation.theme.CurrencyConverterTheme
import com.pranay.currencyconverter.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CurrencyConverterTheme {
                val currencySelectionState by mainViewModel.currencySelectionState.collectAsState()
                with(currencySelectionState.globalState) {
                    if (!loading && errorMessage == null)
                        CurrencyConverterScreen(
                            viewModel = mainViewModel
                        )
                    else
                        ShowLoaderOrError(this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CurrencyConverterTheme {
        Greeting("Android")
    }
}