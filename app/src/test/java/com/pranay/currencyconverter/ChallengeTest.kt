package com.pranay.currencyconverter

import org.junit.Assert
import org.junit.Test

class Sample {
    override fun hashCode(): Int {
        return 1
    }
}

class ChallengeTest {

    /*Given an array nums of n integers, are there elements a, b, c in nums such that a + b + c = 0?

Find all unique triplets in the array which gives the sum of zero.

e.g.

Input: [-1,0,1,2,-1,-4]
Output: [[-1,-1,2],[-1,0,1]]

If input empty array, output will return empty array.

Input: []
Output: []*/
    @Test
    fun findSumPair() {
        val array = arrayOf(-1,0,1,2,-1,-4)
        val resultArray:MutableList<List<Int>> = mutableListOf()

        for(i in 0..array.size-1){
            for(j in i+1..array.size-1)
                for(k in j+1..array.size-1)
                        if (array[i] + array[j] + array[k] == 0)
                            resultArray.add(listOf(array[i], array[j], array[k]))

        }

        resultArray.forEach {
            println(it)
        }




    }
    
    @Test
    fun findSize() {
        val map = mutableMapOf<Sample?,String>()
        map.put(null,"")
        map.put(Sample(),"weee")
        map.put(Sample(),"wese")
        map.put(Sample(),"wsee")
        map.put(Sample(),"we4ee")
        Assert.assertEquals(5,map.size)
    }
}