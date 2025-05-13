package kaosborn.chromafill
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.core.view.MenuProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class ChromafillMenuProvider (private val view:View, private val vm:GridGamesViewModel) : MenuProvider {
    override fun onCreateMenu (menu:Menu, menuInflater:MenuInflater) {
        menuInflater.inflate (R.menu.menu_chromafill, menu)
    }

    override fun onMenuItemSelected (menuItem:MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_chromafill_menu -> {
                val dlg = View.inflate (view.context, R.layout.settings_chromafill, null)

                val newGameButton = dlg.findViewById<Button> (R.id.new_game_btn)
                newGameButton.setOnClickListener { vm.initGame() }

                val boardSizeView = dlg.findViewById<NumberPicker> (R.id.size_setting)
                boardSizeView.minValue = 1
                boardSizeView.maxValue = vm.maxGameSize
                boardSizeView.value = vm.boardSize.value!!
                boardSizeView.setOnValueChangedListener { _, _, q -> vm.boardSizeValue = q }

                MaterialAlertDialogBuilder (view.context, R.style.SettingsDialog)
                    .setTitle(null)
                    .setView(dlg)
                    .show()

                return true
            }
        }
        return false
    }
}
