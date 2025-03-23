package com.chromafill

import org.junit.Test
import org.junit.Assert.*
import com.kaosborn.gridgames.*

class GridTests {
    @Test
    fun gridTest_values() {
        val g12 = CellGrid (1,2)
        assertEquals (1, g12.xSize)
        assertEquals (2, g12.ySize)
        assertEquals ("0\n0", g12.toString())

        val b35 = CellGrid (a35b)
        assertEquals (5, b35.xSize)
        assertEquals (3, b35.ySize)
        assertEquals (1, b35.at(4,1))
        assertTrue (b35.isEquals (a35b))
        assertEquals ("2 0 2 0 1\n2 2 2 0 1\n2 0 0 0 1", b35.toString())
    }

    @Test
    fun gridTest_ctor_with_vals() {
        val grid = CellGrid (1,1)
        assertTrue (grid.isEquals (a11a))

        grid.flood4 (0,0,1)
        assertTrue (grid.isEquals ((a11b)))

        assertEquals ("1", grid.toString())
    }
}

val a11a = mutableListOf (intArrayOf ( 0 ))
val a11b = mutableListOf (intArrayOf ( 1 ))

val a22a = mutableListOf (intArrayOf ( 0,1 ), intArrayOf ( 0,0 ))
val a22b = mutableListOf (intArrayOf ( 2,1 ), intArrayOf ( 2,2 ))

val a35a = mutableListOf (
    intArrayOf ( 1,0,1,0,1 ),
    intArrayOf ( 1,1,1,0,1 ),
    intArrayOf ( 1,0,0,0,1 ))

class FloodTests {
    @Test
    fun floodTest_11a() {
        val grid00 = CellGrid (a11b)
        grid00.flood4 (0,0,0)
        assertEquals ("0", grid00.toString())

        val grid01 = CellGrid (a11b)
        grid01.flood4 (0,0,1)
        assertEquals ("1", grid01.toString())
    }

    @Test
    fun floodTest_11() {
        val g11 = CellGrid (1,1)
        g11.flood4 (0,0,1)
        assertEquals ("1", g11.toString())
    }

    @Test
    fun floodTest_22() {
        val grid = CellGrid (a22a)
        grid.flood4 (0,1,2)
        assertEquals ("2 1\n2 2", grid.toString())
    }

    @Test
    fun floodTest_35a() {
        val g35a = CellGrid(a35a)
        g35a.flood4 (1, 1, 2)
        assertEquals ("2 0 2 0 1\n2 2 2 0 1\n2 0 0 0 1", g35a.toString())
    }

    @Test
    fun floodTest_35b() {
        val g35b = CellGrid (a35a)
        g35b.flood4 (4,2,1)
        assertEquals ("1 0 1 0 1\n1 1 1 0 1\n1 0 0 0 1", g35b.toString())
    }

    @Test
    fun floodTest_35c() {
        val grid2 = CellGrid (a35a)
        assertTrue (grid2.isEquals (a35a))

        grid2.flood4 (1,1,2)
        assertTrue (grid2.isEquals ((a35b)))
    }

    val axx22a = mutableListOf (intArrayOf(0,1), intArrayOf(1,0))
    @Test
    fun dataTest() {
        val g3 = CellGrid (axx22a)
        axx22a[0][0] = 9
        assertEquals(0,g3.at(0,0))
    }
}

val a35b = mutableListOf (
    intArrayOf ( 2,0,2,0,1 ),
    intArrayOf ( 2,2,2,0,1 ),
    intArrayOf ( 2,0,0,0,1 ))
