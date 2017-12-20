package org.gangel.orders.rnd;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public class ProbabilityTest {

    @Test
    public void test3Choises() {
        Long result = Probability.select(Long.class)
            .with(0.1, ()->new Long(1))
            .with(0.2, ()->new Long(2))
            .with(0.7, ()->new Long(3))
            .choose()
            .get();
        
        MatcherAssert.assertThat(result, anyOf(is(3L), is(2L), is(1L)));
    }
    
    @Test
    public void testZeroProb() {
        Long result = Probability.select(Long.class)
                .with(0, ()->new Long(0L))
                .with(0.9, ()->new Long(1L))
                .with(0.1, ()->new Long(2L))
                .choose()
                .get();
        
        MatcherAssert.assertThat(result,  CoreMatchers.anyOf(is(1L), is(2L)));
    }
    
    @Test
    public void test100Prob() {
        Long result = Probability.select(Long.class)
                .with(0, ()->new Long(0L))
                .with(1, ()->new Long(1L))
                .with(0, ()->new Long(2L))
                .choose()
                .get();
        
        MatcherAssert.assertThat(result,  is(1L));
        
    }

}
