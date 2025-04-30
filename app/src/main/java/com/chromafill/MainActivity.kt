package com.chromafill
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.chromafill.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val vm: GridGamesViewModel by viewModels()

    override fun onCreate (savedInstanceState:Bundle?) {
        super.onCreate (savedInstanceState)

        binding = ActivityMainBinding.inflate (layoutInflater)
        setContentView (binding.root)
        setSupportActionBar (binding.toolbar)

        val navCon = findNavController (R.id.nav_host_fragment_content_main)
        appBarConfig = AppBarConfiguration (navCon.graph)
        setupActionBarWithNavController (navCon, appBarConfig)

        val prefs = getPreferences (Context.MODE_PRIVATE)
        if (prefs!=null)
            vm.loadPrefs (prefs)

        vm.loadColors (resources.getIntArray(R.array.color_list))
    }

    override fun onPause() {
        vm.savePrefs (getPreferences (MODE_PRIVATE))
        super.onPause()
    }

    override fun onCreateOptionsMenu (menu:Menu): Boolean {
        menuInflater.inflate (R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected (item:MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected (item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController (R.id.nav_host_fragment_content_main)
        return navController.navigateUp (appBarConfig) || super.onSupportNavigateUp()
    }
}
