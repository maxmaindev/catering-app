package com.example.cateringapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.cateringapp.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private val authVM: AuthViewModel by viewModels()

    private lateinit var binding: ActivityAuthBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplash()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun installSplash() {
        authVM.checkTokenValidation()
        installSplashScreen().setKeepOnScreenCondition {
            val authTokenResult = authVM.authTokenResult.value
            when (authTokenResult) {
                AuthTokenResult.Idle -> {
                    true
                }
                else -> {
                    onSplashDisable(authTokenResult!!)
                    false
                }
            }
        }
    }

    private fun onSplashDisable(value: AuthTokenResult) {
        initNavController()
        if (value is AuthTokenResult.NavigateToMain){
            openHomeActivity()
        }
    }

    fun openHomeActivity(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }

    private fun initNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_activity_auth
        ) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    fun logout() {
        lifecycleScope.launch {
            authVM.destroyPrefs()
            startActivity(Intent(this@AuthActivity, AuthActivity::class.java))
        }
    }

    fun getAnchor(): View {
        return binding.root
    }

}
    //navigation
    // app:navGraph="@navigation/nav_graph" />
//        val navHostFragment = supportFragmentManager.findFragmentById(
//            R.id.nav_host_fragment_activity_main
//        ) as NavHostFragment
//        navController = navHostFragment.navController
//        navController.setGraph(R.navigation.main_nav_graph)

//    private fun createGraph(){
//        val navHostFragment = supportFragmentManager.findFragmentById(
//            R.id.nav_host_fragment_activity_main
//        ) as NavHostFragment
//        val inflater = navHostFragment.navController.navInflater
//        val graph = inflater.inflate(R.navigation.main_nav_graph)
//        //graph.setStartDestination(R.id.nav)
//        navHostFragment.navController.graph = graph
//    }

