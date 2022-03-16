package com.teamhowl.howl;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.teamhowl.howl.models.BlockChain;
import com.teamhowl.howl.models.PendingBlock;
import com.teamhowl.howl.models.StashedBlock;
import com.teamhowl.howl.utilities.Crypto;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BlockChainInstrumentedTest {

    @Test
    public void testAll() {
        // Context of the app under test.
        /*"com.teamhowl.howl" package name*/
        String userAId = Crypto.generateUserId("ADDRESS");

        assert(true);
    }

    @Test
    public void generateUserId() {

        String userAId = Crypto.generateUserId("ADDRESS");
        //String userBId = Crypto.generateUserId("ADDRESS");
        assert(true);
    }
}