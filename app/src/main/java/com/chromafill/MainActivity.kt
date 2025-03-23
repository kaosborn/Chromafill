package com.chromafill

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import com.chromafill.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val vm: GridGamesViewModel by viewModels()

    override fun onCreate (state:Bundle?) {
        super.onCreate (state)

        binding = ActivityMainBinding.inflate (layoutInflater)
        setContentView (binding.root)
        setSupportActionBar (binding.toolbar)

        val navController = findNavController (R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration (navController.graph)
        setupActionBarWithNavController (navController, appBarConfiguration)

        vm.loadColors (resources.getIntArray(R.array.color_list))
    }

    override fun onCreateOptionsMenu (menu:Menu): Boolean {
        menuInflater.inflate (R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected (item:MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController (R.id.nav_host_fragment_content_main)
        return navController.navigateUp (appBarConfiguration) || super.onSupportNavigateUp()
    }
}
