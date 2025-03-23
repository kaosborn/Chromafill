package com.chromafill

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator.*
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chromafill.databinding.FragmentChromafillBinding

class ChromafillFragment : Fragment() {
    private var _binding: FragmentChromafillBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: GridGamesViewModel
    private var isStarting = true

    override fun onCreateView (inflater:LayoutInflater, container:ViewGroup?, savedInstanceState:Bundle?): View {
        _binding = FragmentChromafillBinding.inflate (inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        vm = ViewModelProvider (requireActivity()).get (GridGamesViewModel::class.java)
        binding.pvm = vm
        return binding.root
    }

    override fun onViewCreated (view:View, state:Bundle?) {
        super.onViewCreated (view, state)
        if (! vm.isGame())
            vm.newGame()

        val colorChoiceChangeObserver = Observer<Int?> {
            newColor -> onColorChoiceChange (newColor)
        }
        vm.colorChoice.observe (requireActivity(),colorChoiceChangeObserver)

        binding.buttonReset.setOnClickListener {
            vm.newGame()
            paintBoard()
            vm.chooseColor (vm.at(vm.xRoot,vm.yRoot))
            redrawPalette (vm.at(vm.xRoot,vm.yRoot))
        }

        makeBoard()
        makePalette()
        paintBoard()
    }

    override fun onResume() {
        isStarting = false
        super.onResume()
    }

    private fun makePalette() {
        for (i in 0..<vm.gameColors.size) {
            val v = Button(requireContext())
            val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            lp.width = (resources.getDimension(R.dimen.paletteTileSize)+0.5F).toInt()
            lp.height = (resources.getDimension(R.dimen.paletteTileSize)+0.5F).toInt()
            if (i>0) lp.leftMargin = (resources.getDimension(R.dimen.paletteTileMargin)+0.5F).toInt()
            v.setBackgroundColor (vm.gameColors[i])
            v.setOnClickListener { paletteClickHandler (it as TextView, i) }
            v.layoutParams = lp
            binding.palette.addView (v)
        }
    }

    private fun redrawPalette (lastChoiceIx:Int) {
        for (i in 0..<vm.gameColors.size) {
            val w = binding.palette.getChildAt(i) as Button
            w.isClickable = i!=lastChoiceIx
            w.text = if (i!=lastChoiceIx) "" else "xx"
        }
    }

    private fun makeBoard() {
        for (y in 0..<vm.ySize.value!!)
            for (x in 0..<vm.xSize.value!!) {
                val v = TextView(requireContext())
                v.setTextColor (ContextCompat.getColor(requireContext(),R.color.black))
                v.id = View.generateViewId()
                v.width = (resources.getDimension(R.dimen.cellTileSize)+0.5F).toInt()
                v.height = (resources.getDimension(R.dimen.cellTileSize)+0.5F).toInt()
                v.gravity = Gravity.CENTER

                val p = GridLayout.LayoutParams(GridLayout.spec(y+3),GridLayout.spec(x))
                if (x>0) p.leftMargin = (resources.getDimension(R.dimen.cellTileMargin)+0.5F).toInt()
                if (y>0) p.topMargin = (resources.getDimension(R.dimen.cellTileMargin)+0.5F).toInt()
                v.layoutParams = p
                binding.board.addView (v)
            }
    }

    private fun paintBoard() {
        for (y in 0..<vm.ySize.value!!)
            for (x in 0..<vm.xSize.value!!) {
                val w = binding.board.getChildAt(y*vm.xSize.value!!+x) as TextView
                w.setBackgroundColor (vm.gameColors[vm.at(x,y)%vm.gameColors.size])
                w.text = ""
            }
    }

    private fun repaintBoard (oldColor:Int) {
        for (y in 0..<vm.ySize.value!!)
            for (x in 0..<vm.xSize.value!!) {
                val w = binding.board.getChildAt(y*vm.xSize.value!!+x) as TextView
                //w.text = vm.rankAt(x,y).toString()
                if (vm.rankAt(x,y)!=0) {
                    val oldLevel = vm.gameColors[oldColor]
                    val newLevel = vm.gameColors[vm.at(x,y)%vm.gameColors.size]
                    if (! vm.isContact(x,y)) {
                        val a2 = ofInt(
                            w,
                            "backgroundColor",
                            oldLevel,newLevel)
                        a2.startDelay = (vm.rankAt(x,y)*3).toLong()
                        a2.duration = 25
                        a2.setEvaluator (ArgbEvaluator())
                        a2.start()
                    }
                    else {
                        val a7 = ofInt(0,3)
                        a7.startDelay = (vm.rankAt(x,y)*3).toLong()
                        a7.addUpdateListener {
                            val q = it.animatedValue as Int
                            if (q==0 || q==2)
                                w.text = "$"
                            else if (q==1)
                                w.text = "\$\$"
                            else
                                w.text = ""
                        }
                        a7.start()

//                        val a8 = ofInt(w,"backgroundColor",oldLevel,newLevel)
//                        a8.startDelay = (vm.rankAt(x,y)*3+50).toLong()
//                        a8.duration = 100
//                        a8.setEvaluator (ArgbEvaluator())

//                        val b1 = AnimatorSet()
//                        b1.playSequentially(a7,a8)
//                        b1.playTogether(a7,a8)
//                        b1.start()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val banner1 = "WINNER"
    private val banner2 = "LOSER "
    private fun onColorChoiceChange (selectedColorIx:Int?) {
        if (selectedColorIx==null) {
            val banner = if (vm.isWon) banner1 else banner2
            for ((i,v) in binding.palette.children.withIndex()) {
                v.isClickable = false
                (v as Button).text = banner[i%banner.length].toString()
            }
        }
        else {
            val w = binding.palette.getChildAt(selectedColorIx) as Button
            w.text = "X"
            w.isClickable = false
        }
    }

    private fun paletteClickHandler (w:TextView, thisChoiceIx:Int) {
        w.isClickable = false
        val targetColor = vm.at(vm.xRoot,vm.yRoot)
        vm.fill (thisChoiceIx)
        repaintBoard (targetColor)

        if (vm.isOneColor())
            vm.endGame()
        else {
            if (vm.colorChoice.value!=null && vm.colorChoice.value!!>=0) {
                val priorChoice = binding.palette.getChildAt(vm.colorChoice.value!!) as Button
                priorChoice.isClickable = true
                priorChoice.text = ""
            }
            vm.chooseColor (thisChoiceIx)
        }
    }
}
