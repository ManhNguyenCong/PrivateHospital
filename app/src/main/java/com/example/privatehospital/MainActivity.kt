package com.example.privatehospital

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.privatehospital.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.contentMain.toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.personalInfoFragment,
                R.id.settingFragment,
                R.id.loginFragment),
            binding.mainDrawerLayout
        )

        // Retrieve NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the action bar for use with the NavController
        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )

        setupNavigationView()
        binding.navView.setupWithNavController(navController)
    }

    /**
     * Handle navigation when the user chooses Up from the action bar.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home_fragment, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * This function is used to setup navigation view when login/no login
     */
    private fun setupNavigationView() = binding.navView.apply {
        // TODO check login ?
        if(false) {
            menu.findItem(R.id.personalInfoFragment).isEnabled = true
            menu.findItem(R.id.historyFragment).isEnabled = true
            menu.findItem(R.id.loginFragment).apply {
                title = context.getString(R.string.logout)
                icon = AppCompatResources.getDrawable(context, R.drawable.baseline_logout_24)
            }
        } else {
            menu.findItem(R.id.personalInfoFragment).isEnabled = false
            menu.findItem(R.id.historyFragment).isEnabled = false
            menu.findItem(R.id.loginFragment).apply {
                title = context.getString(R.string.login)
                icon = AppCompatResources.getDrawable(context, R.drawable.baseline_login_24)
            }
        }
    }
}