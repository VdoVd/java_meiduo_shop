package com.leyou.auth.utils;

import org.junit.Test;

public class NumberTest {

    @Test
    public void testMax(){

        int a = 5;

        double b = a;


        System.out.println(a==b);




        long longValue = Long.MAX_VALUE;

        System.out.println("longValue = " + longValue);

        double doubleValue = longValue;


        System.out.println(longValue==doubleValue);

        System.out.println("doubleValue = " + doubleValue);
    }
}
