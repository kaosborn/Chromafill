package com.chromafill

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kaosborn.gridgames.CellGrid

class GridGamesViewModel (private val state:SavedStateHandle) : ViewModel() {
    private lateinit var grid:CellGrid
    lateinit var gameColors:List<Int>; private set
    private val newGameSize = 14
    var isWon = false; private set
    val xRoot = 0
    val yRoot = 0

    private val _xSize = MutableLiveData(newGameSize)
    val xSize:LiveData<Int> = _xSize

    private val _ySize = MutableLiveData(newGameSize)
    val ySize:LiveData<Int> = _ySize

    private val _score = MutableLiveData(-1)
    val score:LiveData<Int> = _score
    private var scoreValue:Int
        get() =_score.value!!
        set (value) {
            _score.value = value
            state[SCORE_KEY] = value
        }

    private val _highScore = MutableLiveData(0)
    val highScore:LiveData<Int> = _highScore
    private var highScoreValue:Int
        get() =_highScore.value!!
        set (value) {
            _highScore.value = value
            state[HIGH_SCORE_KEY] = value
        }

    private val _moves = MutableLiveData(-1)
    val moveCount:LiveData<Int> = _moves
    private var movesValue:Int
        get() =_moves.value!!
        set (value) {
            _moves.value = value
            state[MOVES_KEY] = value
        }

    private val _lowMoves = MutableLiveData(-1)
    val lowMoves:LiveData<Int> = _lowMoves
    private var lowMovesValue:Int
        get() =_lowMoves.value!!
        set (value) {
            _lowMoves.value = value
            state[LOW_MOVES_KEY] = value
        }

    private val _colorChoice = MutableLiveData<Int?>(null)
    val colorChoice:LiveData<Int?> = _colorChoice

    init {
        if (! this::gameColors.isInitialized) {
            val c = state.get<IntArray>(COLORS_KEY)
            if (c!=null)
                gameColors = c.toList()
        }

        val data:MutableList<IntArray> = mutableListOf()
        val enums:MutableList<IntArray> = mutableListOf()

        var xs = 0
        while (true) {
            val dr = state.get<IntArray>(DATA_KEY_PREFIX+data.size.toString()) ?: break
            val er = state.get<IntArray>(ENUMS_KEY_PREFIX+data.size.toString()) ?: break
            data.add (dr)
            enums.add (er)
            if (xs < data.size)
                xs = data.size
        }

        _ySize.value = data.size
        _xSize.value = xs

        if (data.size>0) {
            grid = CellGrid (data, enums)

            isWon = grid.isMonochrome()
            if (! isWon)
                _colorChoice.value = grid.at(xRoot,yRoot)

            scoreValue = state.get<Int>(SCORE_KEY) ?: 0
            highScoreValue = state.get<Int>(HIGH_SCORE_KEY) ?: 0
            movesValue = state.get<Int>(MOVES_KEY) ?: 0
            lowMovesValue = state.get<Int>(LOW_MOVES_KEY) ?: 0
        }
    }

    companion object {
        private const val COLORS_KEY = "COLORS"
        private const val SCORE_KEY = "SCORE"
        private const val HIGH_SCORE_KEY = "HIGH_SCORE"
        private const val MOVES_KEY = "MOVES"
        private const val LOW_MOVES_KEY = "LOW_MOVES"
        private const val DATA_KEY_PREFIX = "DATA_"
        private const val ENUMS_KEY_PREFIX = "ENUMS_"
    }

    fun loadColors (colors:IntArray) {
        gameColors = colors.toList()
        state[COLORS_KEY] = colors
    }

    fun chooseColor (color:Int?) {
        if (color==null)
            _colorChoice.value = null
        else {
            var c = color%gameColors.size
            if (c<0) c += gameColors.size
            _colorChoice.value = c
        }
    }

    fun isGame() = this::grid.isInitialized
    fun at (x:Int, y:Int) = grid.at(x,y)
    fun rankAt (x:Int, y:Int) = grid.rankAt(x,y)
    fun isContact (x:Int, y:Int) = grid.isContact(x,y)
    fun isOneColor() = grid.isMonochrome()

    fun newGame() {
        isWon = false
        grid = CellGrid (newGameSize,newGameSize)
        grid.randomize (gameColors.size)
        _xSize.value = grid.xSize
        _ySize.value = grid.ySize
        movesValue = 0
        scoreValue = 0
        grid.crawl4 (xRoot,yRoot,grid.at(xRoot,yRoot))
        addPoints (grid.maxEnumeration)
    }

    fun endGame() {
        if (lowMovesValue<0 || lowMovesValue in 0..<movesValue)
            lowMovesValue = movesValue
        isWon = true
        _colorChoice.value =  null
    }

    private fun addPoints (tileCount:Int) {
        scoreValue += tileCount * (tileCount + 1)
        if (highScoreValue<scoreValue) {
            highScoreValue = scoreValue
            state[HIGH_SCORE_KEY] = highScoreValue
        }
    }

    private fun saveData() {
        for (y in 0..<_ySize.value!!) {
            state[DATA_KEY_PREFIX+y.toString()] = grid.getData(y)
            state[ENUMS_KEY_PREFIX+y.toString()] = grid.getEnums(y)
        }
    }

    fun fill (thisChoiceIx:Int): Int {
        val contacts = grid.flood4 (xRoot,yRoot,thisChoiceIx)
        movesValue++
        saveData()
        addPoints (contacts)
        return grid.maxEnumeration
    }
}
