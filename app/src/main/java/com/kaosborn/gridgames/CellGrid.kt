package com.kaosborn.gridgames

class CellGrid() {
    private var data:MutableList<IntArray> = mutableListOf()
    private var enums:MutableList<IntArray> = mutableListOf()
    private var fillSize = 0
    var maxEnumeration = 0; private set
    var xSize = 0; private set
    var ySize = 0; private set
    private var area = 0

    constructor (xSize:Int, ySize:Int) : this() {
        this.xSize = xSize
        this.ySize = ySize
        this.area = xSize*ySize
        for (y in 0..<ySize) {
            this.data.add (IntArray(xSize))
            this.enums.add (IntArray(xSize))
        }
    }

    constructor (data:MutableList<IntArray>) : this() {
        for (dr in data) {
            this.area += dr.size
            this.data.add (dr.copyOf())
            this.enums.add (IntArray(dr.size))
            if (this.xSize < dr.size)
                this.xSize = dr.size
        }
        this.ySize = data.size
    }

    constructor (data:MutableList<IntArray>, enums:MutableList<IntArray>) : this() {
        var filledColor = -1
        if (data.size != enums.size)
            throw IllegalArgumentException ("size mismatch")
        for (y in data.indices) {
            val dr = data[y].copyOf()
            val er = enums[y].copyOf()
            if (dr.size != er.size)
                throw IllegalArgumentException ("size mismatch at $y")
            if (this.xSize < dr.size)
                this.xSize = dr.size
            this.area += dr.size
            this.data.add (dr)
            this.enums.add (er)
            for (x in dr.indices) {
                val t = er[x]
                if (t > 0) {
                    if (filledColor < 0)
                        filledColor = dr[x]
                    else if (filledColor != dr[x])
                        throw IllegalArgumentException ("color mismatch")
                    if (this.maxEnumeration < t)
                        this.maxEnumeration = t
                }
            }
        }
        this.ySize = data.size
        this.fillSize = this.maxEnumeration
    }

    fun at (x:Int, y:Int) = data[y][x]
    fun rankAt (x:Int, y:Int) = enums[y][x]
    fun isContact (x:Int, y:Int) = enums[y][x]>fillSize
    fun isConstant() = area==maxEnumeration
    fun getSize (y:Int) = data[y].size
    fun getData (y:Int): IntArray = data[y].copyOf()
    fun getEnums (y:Int): IntArray = enums[y].copyOf()

    fun randomize (depth:Int) {
        maxEnumeration = 0
        for (y in data.indices)
            for (x in data[y].indices) {
                data[y][x] = (0..<depth).random()
                enums[y][x] = 0
            }
    }

    fun isEquals (rval:MutableList<IntArray>): Boolean {
        if (rval.size!=data.size)
            return false
        for (y in data.indices) {
            val dataRow = data[y]
            if (dataRow.size!=rval[y].size)
                return false
            for (x in dataRow.indices)
                if (dataRow[x]!=rval[y][x])
                    return false
        }
        return true
    }

    fun flood4 (x:Int, y:Int, replacementColor:Int): Int {
        if (x<0 || y<0 || y>=data.size || x>=data[y].size)
            return -1
        fillSize = maxEnumeration
        flood4R (x,y, data[y][x], replacementColor)
        return maxEnumeration - fillSize
    }

    private fun flood4R (x:Int, y:Int, oldColor:Int, newColor:Int) {
        when (data[y][x]) {
            newColor ->
                crawl4R (x,y,newColor)
            oldColor -> {
                data[y][x] = newColor
                if (x>0)
                    flood4R (x-1,y,oldColor,newColor)
                if (y>0 && x<data[y-1].size)
                    flood4R (x,y-1,oldColor,newColor)
                if (y+1<data.size && x<data[y+1].size)
                    flood4R (x,y+1,oldColor,newColor)
                if (x+1<data[y].size)
                    flood4R (x+1,y,oldColor,newColor)
            }
        }
    }

    fun crawl4 (x:Int, y:Int, color:Int) {
        crawl4R (x,y,color)
        fillSize = maxEnumeration
    }

    private fun crawl4R (x:Int, y:Int, color:Int) {
        if (enums[y][x]!=0)
            return
        maxEnumeration++
        enums[y][x] = maxEnumeration
        if (x>0 && data[y][x-1]==color)
            crawl4R (x-1,y,color)
        if (y>0 && x<data[y-1].size && data[y-1][x]==color)
            crawl4R (x,y-1,color)
        if (y+1<data.size && x<data[y+1].size && data[y+1][x]==color)
            crawl4R (x,y+1,color)
        if (x+1<data[y].size && data[y][x+1]==color)
            crawl4R (x+1,y,color)
    }

    override fun toString(): String {
        var result = ""
        var y = 0
        val ySizeActual = data.size
        while (true) {
            val xSizeActual = data[y].size
            var x = 0
            while (true) {
                result += data[y][x].toString()
                x++
                if (x>=xSizeActual)
                    break
                result += " "
            }
            y++
            if (y>=ySizeActual)
                break
            result += "\n"
        }
        return result
    }
}
