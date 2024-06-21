package com.example.cateringapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cateringapp.core.UserRolePrefs
import com.example.cateringapp.databinding.ActivityHomeBinding
import com.example.cateringapp.ui.client.home.business.BusinessesViewModel
import com.example.cateringapp.ui.client.orders.OrdersViewModel
import com.example.cateringapp.ui.client.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val ordersViewModel: OrdersViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val businessViewModel: BusinessesViewModel by viewModels()

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preloadInfo()

        initNavController()

    }

    private fun preloadInfo() {
        val basketRes = ordersViewModel.loadBasket()
        sharedViewModel.loadCurrentUser()
        profileViewModel.getMyUserInfo()

        lifecycleScope.launch(Dispatchers.IO) {
            basketRes.join()
            withContext(Dispatchers.Main) {
                if (!ordersViewModel.currentOrder.isLocal) {
                    setOrderBadge(1, true)
                }
            }
        }
    }

    private fun initNavController() {
        lifecycleScope.launch(Dispatchers.IO) {
            val role = authViewModel.getUserRole()
            if (role == null)
                logout()
            else{
                withContext(Dispatchers.Main){
                    val navHostFragment = supportFragmentManager.findFragmentById(
                        R.id.nav_host_fragment_activity_home
                    ) as NavHostFragment
                    navController = navHostFragment.navController
                    if (role == UserRolePrefs.ADMIN.role) {
                        navController.setGraph(R.navigation.admin_nav_graph)
                        isAdmin = true
                        businessViewModel.loadSelectedBusiness()
                        setupBottomNav(isAdmin = true)
                    } else {
                        isAdmin = false
                        navController.setGraph(R.navigation.user_nav_graph)
                        setupBottomNav(isAdmin = false)
                    }
                }
            }
        }
    }


    private fun setupBottomNav(isAdmin: Boolean) {
        // Setup the bottom navigation view with navController
        val menu = if (isAdmin) R.menu.admin_bottom_nav_menu else R.menu.user_bottom_nav_menu
        binding.navView.inflateMenu(menu)
        binding.navView.setupWithNavController(navController)

        val topDest = if (isAdmin) adminSet else userSet
        appBarConfiguration = AppBarConfiguration(topDest)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    fun setOrderBadge(value : Int = 0, isVisible: Boolean = true){
        if (isAdmin){
            val badge = binding.navView.getOrCreateBadge(R.id.admin_orders)
            badge.isVisible = isVisible
            badge.number = value
        }else{
            val badge = binding.navView.getOrCreateBadge(R.id.orders)
            badge.isVisible = isVisible
            badge.number = value
        }
    }

    fun getBottomNav() = binding.navView
    fun logout() {
        lifecycleScope.launch {
            authViewModel.destroyPrefs()
            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
        }
    }



    val adminSet = setOf(
        R.id.admin_home,
        R.id.admin_booking,
        R.id.admin_orders,
        R.id.admin_profile
    )

    val userSet = setOf(
        R.id.home,
        R.id.booking,
        R.id.orders,
        R.id.profile
    )

}