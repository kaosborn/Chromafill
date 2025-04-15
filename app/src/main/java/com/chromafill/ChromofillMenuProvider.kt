package com.chromafill

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.core.view.MenuProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder

open class ChromofillMenuProvider (private val view:View, private val vm:GridGamesViewModel) : MenuProvider {
    override fun onCreateMenu (menu:Menu, menuInflater:MenuInflater) {
        menuInflater.inflate (R.menu.menu_chromofill, menu)
    }

    override fun onMenuItemSelected (menuItem:MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_chromofill_menu -> {
                val dlg = View.inflate (view.context, R.layout.settings_chromofill, null)

                val newGameButton = dlg.findViewById<Button> (R.id.new_game_btn)
                newGameButton.setOnClickListener {
                    vm.initGame()
//                    makeBoard()
//                    paintBoard()
//                    paintPalette (vm.at(vm.xRoot,vm.yRoot))
                    //vm.movesValue = 0
                    //TODO vm.newGame()
                }

                val boardSizeView = dlg.findViewById<NumberPicker> (R.id.size_setting)
                boardSizeView.minValue = 2
                boardSizeView.maxValue = 14
                boardSizeView.value = vm.boardSizeValue
                boardSizeView.setOnValueChangedListener {_, _, q -> vm.boardSizeValue = q }

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
