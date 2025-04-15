package com.chromafill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kaosborn.gridgames.CellGrid

class GridGamesViewModel (private val state:SavedStateHandle) : ViewModel() {
    private lateinit var grid:CellGrid
    lateinit var gameColors:List<Int>; private set
    private val maxGameSize = 20
    var isGameActive = false; private set
    val xRoot = 0
    val yRoot = 0

    private val _boardSize = MutableLiveData(14)
    val boardSize:LiveData<Int> = _boardSize
    var boardSizeValue:Int = 14
        get() = _boardSize.value!!
        set (value) {
            if (value in 1..maxGameSize) {
                if (value!=field) {
                    lowMovesValue = -1
                }
                _boardSize.value = value
                state[Y_SIZE_KEY] = value
            }
        }

    private val _score = MutableLiveData(-1)
    val score:LiveData<Int> = _score
    private var scoreValue:Int
        get() = _score.value!!
        set (value) {
            _score.value = value
            state[SCORE_KEY] = value
        }

    private val _highScore = MutableLiveData(0)
    val highScore:LiveData<Int> = _highScore
    private var highScoreValue:Int
        get() = _highScore.value!!
        set (value) {
            _highScore.value = value
            state[HIGH_SCORE_KEY] = value
        }

    private val _moves = MutableLiveData(-1)
    val moves:LiveData<Int> = _moves
    var movesValue:Int
        get() = _moves.value!!
        set (value) {
            _moves.value = value
            state[MOVES_KEY] = value
            state[Y_SIZE_KEY] = dataHeight()
            for (y in 0..<dataHeight()) {
                state[DATA_KEY_PREFIX+y.toString()] = grid.getData(y)
                state[ENUMS_KEY_PREFIX+y.toString()] = grid.getEnums(y)
            }
        }

    private val _lowMoves = MutableLiveData(-1)
    val lowMoves:LiveData<Int> = _lowMoves
    private var lowMovesValue:Int
        get() = _lowMoves.value!!
        set (value) {
            _lowMoves.value = value
            state[LOW_MOVES_KEY] = value
        }

    private val _colorChoice = MutableLiveData<Int?>(null)
    val colorChoice:LiveData<Int?> = _colorChoice
    var colorChoiceValue:Int?
        get() = _colorChoice.value!!
        set (value) {
            if (value==null)
                _colorChoice.value = null
            else {
                val c = value % gameColors.size
                _colorChoice.value = if (c>=0) c else c + gameColors.size
            }
        }

    init {
        if (! this::gameColors.isInitialized) {
            val c = state.get<IntArray>(COLORS_KEY)
            if (c!=null)
                gameColors = c.toList()
        }

        val data:MutableList<IntArray> = mutableListOf()
        val enums:MutableList<IntArray> = mutableListOf()
        for (r in 0..<(state.get<Int>(Y_SIZE_KEY) ?: 0)) {
            val dr = state.get<IntArray>(DATA_KEY_PREFIX+data.size.toString()) ?: throw IllegalArgumentException ("missing data row")
            val er = state.get<IntArray>(ENUMS_KEY_PREFIX+data.size.toString()) ?: throw IllegalArgumentException ("missing enums row")
            if (dr.size!=er.size)
                throw IllegalArgumentException ("row size mismatch")
            data.add (dr)
            enums.add (er)
        }

        if (data.size>0) {
            grid = CellGrid (data, enums)
            if (! grid.isConstant()) {
                isGameActive = true
                _colorChoice.value = grid.at(xRoot,yRoot)
            }

            _score.value = state.get<Int>(SCORE_KEY) ?: -1
            _highScore.value = state.get<Int>(HIGH_SCORE_KEY) ?: 0
            _moves.value = state.get<Int>(MOVES_KEY) ?: -1
            _lowMoves.value = state.get<Int>(LOW_MOVES_KEY) ?: 0
        }
    }

    companion object {
        private const val COLORS_KEY = "COLORS"
        private const val SCORE_KEY = "SCORE"
        private const val HIGH_SCORE_KEY = "HIGH_SCORE"
        private const val MOVES_KEY = "MOVES"
        private const val LOW_MOVES_KEY = "LOW_MOVES"
        private const val Y_SIZE_KEY = "Y_SIZE"
        private const val DATA_KEY_PREFIX = "DATA_"
        private const val ENUMS_KEY_PREFIX = "ENUMS_"
    }

    fun loadColors (colors:IntArray) {
        gameColors = colors.toList()
        state[COLORS_KEY] = colors
    }

    fun dataHeight() = grid.ySize
    fun dataWidth (y:Int) = grid.getSize(y)

    fun isGame() = this::grid.isInitialized
    fun at (x:Int, y:Int) = grid.at(x,y)
    fun rankAt (x:Int, y:Int) = grid.rankAt(x,y)
    fun isContact (x:Int, y:Int) = grid.isContact(x,y)
    fun isMonochrome() = grid.isConstant()

    fun initGame() {
        grid = CellGrid (boardSizeValue,boardSizeValue)
        grid.randomize (gameColors.size)
        grid.crawl4 (xRoot,yRoot,grid.at(xRoot,yRoot))
        _score.value = 0
        addPoints (grid.maxEnumeration)
        movesValue = 0
        colorChoiceValue = grid.at(xRoot,xRoot)
        isGameActive = ! grid.isConstant()
    }

    fun endGame() {
        if (lowMovesValue<0 || lowMovesValue in 0..<movesValue)
            lowMovesValue = movesValue
        isGameActive = false
        colorChoiceValue = null
    }

    private fun addPoints (tileCount:Int) {
        scoreValue += tileCount * (tileCount + 1)
        if (highScoreValue < scoreValue)
            highScoreValue = scoreValue
    }

    fun fill (thisChoiceIx:Int): Int {
        val contacts = grid.flood4 (xRoot,yRoot,thisChoiceIx)
        movesValue++
        addPoints (contacts)
        return grid.maxEnumeration
    }
}
