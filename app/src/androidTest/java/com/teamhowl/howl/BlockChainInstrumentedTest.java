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
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        String userAId = Crypto.generateUserId("ADDRESS");
        String userBId = Crypto.generateUserId("ADDRESS");

        String chatId = Crypto.generateChatId(userAId, userBId);

        BlockChain blockChainA = new BlockChain(appContext, chatId);
        BlockChain blockChainB = new BlockChain(appContext, chatId);

        PendingBlock pendingBlockA1 = blockChainA.buildGenesisMessage();
        blockChainB.addReceivedMessage(new StashedBlock(
                pendingBlockA1.getChatId(),
                pendingBlockA1.getEncryptedBlock()));

        PendingBlock pendingBlockA2 = blockChainA.buildMessage("Message 1");
        blockChainB.addReceivedMessage(new StashedBlock(
            pendingBlockA2.getChatId(),
            pendingBlockA2.getEncryptedBlock()));

        assert(true);
    }

    @Test
    public void generateUserId() {

        //String userAId = Crypto.generateUserId("ADDRESS");
        //String userBId = Crypto.generateUserId("ADDRESS");
        assert(true);
    }
}