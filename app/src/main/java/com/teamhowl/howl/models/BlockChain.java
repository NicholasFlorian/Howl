package com.teamhowl.howl.models;

import android.content.Context;
import android.util.Log;

import com.teamhowl.howl.controllers.PendingBlockDao;
import com.teamhowl.howl.controllers.StashedBlockDao;
import com.teamhowl.howl.repositories.BlockRoomDatabase;
import com.teamhowl.howl.utilities.Key;

import java.sql.Array;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockChain {

    private final String TAG = "HOWL :: BlockChain";
    private final String TAG_JNI = "HOWL :: BlockChain :: JNI";

    private static Pattern REGEX_MESSAGE;
    private static Pattern REGEX_TIME;

    static {
        REGEX_MESSAGE = Pattern.compile("\\\"message\\\":\\\"(.*)\\\"");
        REGEX_TIME = Pattern.compile("\\\"time\\\":(.*)");

        System.loadLibrary("howl");
    }

    private String chatId;
    private Context context;
    private BlockRoomDatabase blockRoomDatabase;
    private ArrayList<Message> messages;

    /** Native functions **/

    // returns plaintext blocks
    private native String[] cBuildReceivedMessages(
            String chatId,
            String[] encryptedBlocks,
            String privateKey);

    // 1 for encrypted block 0 for plaintext block
    private native String[] cBuildSentMessage(
            String chatId,
            String[] plaintextBlocks,
            String message,
            String publicKey);

    private native String[] cBuildGenesisBlock(
            String chatId,
            String publicKey);


    /** Java Code **/
    public BlockChain(Context context, String chatId) {
        Log.d(TAG, "Construct BlockChain");

        this.chatId = chatId;
        this.context = context;
        this.blockRoomDatabase = BlockRoomDatabase.getDatabase(context);
        this.messages = new ArrayList<>();
    }

    public synchronized void  rerefresh(){

        messages.clear();
    }

    public synchronized void buildAllMessages() {
        Log.d(TAG, "Build All Messages");

        PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "Sent Messages");

        int i = 0;
        for(PendingBlock block : pendingBlockDao.findBlocksByChatId(chatId)){

            if(i == 0){
                i++;
                continue;
            }

            // JSON parsing.
            String plaintextBlock = block.getPlaintextBlock();

            Log.d(TAG, "\n" + plaintextBlock);
            Matcher messageMatcher = REGEX_MESSAGE.matcher(plaintextBlock);
            Matcher timeMatcher = REGEX_TIME.matcher(plaintextBlock);

            messageMatcher.find();
            timeMatcher.find();

            String message = messageMatcher.group(1);
            Date date = new Date(Long.parseLong(timeMatcher.group(1)));

            Message newMessage = new Message(message, date);
            messages.add(newMessage);
        }

        buildReceivedMessages();

        Collections.sort(messages);
    }

    private synchronized void buildReceivedMessages() {
        Log.d(TAG, "Build Received Messages");

        StashedBlockDao stashedBlockDao = blockRoomDatabase.stashedBlockDao();

        ArrayList<String> encryptedBlocks = new ArrayList<>();
        for(StashedBlock block : stashedBlockDao.findBlocksByChatId(chatId)){

            encryptedBlocks.add(block.getEncryptedBlock());
        }

        Log.d(TAG_JNI, "cBuildReceivedMessages()");
        String[] plaintextBlocks = cBuildReceivedMessages(
            chatId,
            encryptedBlocks.toArray(new String[encryptedBlocks.size()]),
            Key.retrieve(context, chatId, Key.PRIVATE_KEY, Key.LOCAL_KEY));

        Log.d(TAG, "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        Log.d(TAG, "Received Messages");

        int i = 0;
        for(String plaintextBlock : plaintextBlocks){

            if(i == 0){
                i++;
                continue;
            }

            Log.d(TAG, "\n" + plaintextBlock);
            Matcher messageMatcher = REGEX_MESSAGE.matcher(plaintextBlock);
            Matcher timeMatcher = REGEX_TIME.matcher(plaintextBlock);

            if(messageMatcher.find() && timeMatcher.find()) {

                String message = messageMatcher.group(1);
                Date date = new Date(Long.parseLong(timeMatcher.group(1)));

                Message newMessage = new Message(message, date, new Date(0));
                messages.add(newMessage);
            }
            else {
                Message newMessage = new Message("FAILED_MESSAGE", new Date(0), new Date(0));
                messages.add(newMessage);
            }
        }
    }

    public synchronized PendingBlock buildSentMessage(String message){
        Log.d(TAG, "Build Sent Message");

        PendingBlockDao pendingBlockDao = blockRoomDatabase.pendingBlockDao();

        ArrayList<String> plaintextBlocks = new ArrayList<>();
        for(PendingBlock block : pendingBlockDao.findBlocksByChatId(chatId)){

           plaintextBlocks.add(block.getPlaintextBlock());
        }

        Log.d(TAG_JNI, "cBuildSentMessage()");
        String[] blocks = cBuildSentMessage(
            chatId,
            plaintextBlocks.toArray(new String[plaintextBlocks.size()]),
            message,
            Key.retrieve(context, chatId, Key.PUBLIC_KEY, Key.FOREIGN_KEY));


        PendingBlock newBlock = new PendingBlock(chatId, blocks[0], blocks[1]);

        return newBlock;
    }

    public synchronized PendingBlock buildGenesisBlock(){
        Log.d(TAG, "Build Genesis Block");

        Log.d(TAG_JNI, "cBuildGenesisBlock()");
        String[] blocks = cBuildGenesisBlock(
            chatId,
            Key.retrieve(context, chatId, Key.PUBLIC_KEY, Key.FOREIGN_KEY));

        PendingBlock newBlock = new PendingBlock(chatId, blocks[0], blocks[1]);

        return newBlock;
    }

    public synchronized ArrayList<Message> getMessages() {
        return messages;
    }

}
