package com.doteam.usingbeacon;

import org.altbeacon.beacon.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by voidblue on 2016-11-02.
 */
public class Beaconsupport {
    private static final List<Long> UNMODIFIABLE_LIST_OF_LONG = Collections.unmodifiableList(new ArrayList());
    private static final List<Identifier> UNMODIFIABLE_LIST_OF_IDENTIFIER = Collections.unmodifiableList(new ArrayList());
    int id;
    int distance;
    boolean isfirst=false;
    void getatrribute(int x, int y, boolean f){
        id = x;
        distance = y;
        isfirst = f;
    }
}
