package org.com.hcmurs

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.com.hcmurs.ui.screens.login.LoginViewModel
import org.com.hcmurs.ui.theme.HcmursAppTheme
import org.com.hcmurs.utils.CurrencyManager
import org.com.hcmurs.utils.LanguageManager
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var currencyManager: CurrencyManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize currency manager with language manager
        LanguageManager.setCurrencyManager(currencyManager)

        val currentLang = LanguageManager.getLocale(this)
        LanguageManager.setLocale(this, currentLang)

        // Initialize exchange rate on app start
        lifecycleScope.launch {
            currencyManager.updateExchangeRate()
        }

        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51RhDaTR1Z2NQalNLXVx9pppFy2tEDMw5ehfDwdeMl6K0yEcHOsR3u5UJ7kpHhHex1MPht1PaCYOGZZqTlS4lDK6c00EQJWcg0W" //
        )

        enableEdgeToEdge()

        // Handle OAuth success from redirect activity
        try{
            intent?.let { intent ->
                if (intent.getBooleanExtra("oauth_success", false)) {
                    // Use the new method to handle sign-in
                    loginViewModel.handleGoogleSignInResult(intent)
                }

                val error = intent.getStringExtra("oauth_error")
                if (error != null) {
                    // Update LoginViewModel with error
                    loginViewModel.updateLoginError(error)
                }
            }
        }catch (e: Exception){
            e.message?.let { Log.d("Main Activity:", it) }
        }

        setContent {
            HcmursAppTheme {
                Navigation()
            }
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val languageCode = LanguageManager.getLocale(newBase)
            val locale = Locale(languageCode)
            val config = Configuration(newBase.resources.configuration)
            config.setLocale(locale)
            val context = newBase.createConfigurationContext(config)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }
}

