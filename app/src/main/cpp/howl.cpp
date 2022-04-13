
#include <jni.h>
#include "blockChain.h"

/** Crypto Java Library */
extern "C" JNIEXPORT void JNICALL
Java_com_teamhowl_howl_utilities_Crypto_loadSSL(JNIEnv *env, jclass clazz) {

    howl::BlockChain::loadSSL();
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_teamhowl_howl_utilities_Crypto_generateKeyPair(JNIEnv *env, jclass clazz) {

    jobjectArray keyList;
    char* publicKey = nullptr;
    char* privateKey = nullptr;

    howl::BlockChain::loadSSL();
    howl::BlockChain::generateKeyPair(&publicKey, &privateKey);

    keyList = (jobjectArray)env->NewObjectArray(
        2,
        env->FindClass("java/lang/String"),
        env->NewStringUTF(""));

    env->SetObjectArrayElement(
        keyList,
        0,
        env->NewStringUTF(publicKey));

    env->SetObjectArrayElement(
        keyList,
        1,
        env->NewStringUTF(privateKey));

    free(publicKey);
    free(privateKey);

    return keyList;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_utilities_Crypto_generateChatId(JNIEnv *env, jclass clazz,
    jstring jLocalUserId,
    jstring jForeignUserId) {

    char* chatId;
    char* localId;
    char* foreignId;
    jstring jChatId;

    localId = const_cast<char *>(env->GetStringUTFChars(jLocalUserId, 0));
    foreignId = const_cast<char *>(env->GetStringUTFChars(jForeignUserId, 0));

    howl::BlockChain::generateChatId(&chatId, localId, foreignId);
    jChatId = env->NewStringUTF(chatId);

    env->ReleaseStringUTFChars(jLocalUserId, localId);
    env->ReleaseStringUTFChars(jForeignUserId, foreignId);

    return jChatId;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_utilities_Crypto_generateUserId(JNIEnv *env, jclass clazz,
    jstring jLocalAddress) {

    char* localAddress;
    char* userId;
    jstring jUserId;

    localAddress = const_cast<char *>(env->GetStringUTFChars(jLocalAddress, 0));

    howl::BlockChain::generateUserId(&userId, localAddress);
    jUserId = env->NewStringUTF(userId);

    env->ReleaseStringUTFChars(jLocalAddress, localAddress);

    return jUserId;
}



/** BlockChain Java Library */
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_teamhowl_howl_models_BlockChain_cBuildReceivedMessages(JNIEnv *env, jobject thiz,
    jstring jChatId,
    jobjectArray jEncryptedBlocks,
    jstring jPrivateKey) {

    howl::BlockChain::loadSSL();

    char* chatId = const_cast<char *>(env->GetStringUTFChars(jChatId, 0));
    howl::BlockChain* blockChain = new howl::BlockChain(chatId);

    jsize encryptedSize = env->GetArrayLength(jEncryptedBlocks);

    char* privateKey = const_cast<char *>(env->GetStringUTFChars(jPrivateKey, 0));

    jobjectArray jBlocks = (jobjectArray)env->NewObjectArray(
            encryptedSize,
            env->FindClass("java/lang/String"),
            env->NewStringUTF(""));

    for(jsize i = 0; i < encryptedSize; i++){

        jstring jEncryptedBlock = static_cast<jstring>(
                env->GetObjectArrayElement(jEncryptedBlocks, i));

        char* encryptedBlock = const_cast<char *>(
                env->GetStringUTFChars(jEncryptedBlock, 0));

        blockChain->addReceivedBlock(encryptedBlock, privateKey);

        char* plaintextBlock = blockChain->getLastReceivedBlock()->toJSON();

        env->SetObjectArrayElement(
                jBlocks,
                i,
                env->NewStringUTF(plaintextBlock));

        env->ReleaseStringUTFChars(jEncryptedBlock, encryptedBlock);
    }

    env->ReleaseStringUTFChars(jChatId, chatId);
    env->ReleaseStringUTFChars(jPrivateKey, privateKey);
    howl::BlockChain::freeBlockChain(blockChain);

    return jBlocks;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_teamhowl_howl_models_BlockChain_cBuildSentMessage(JNIEnv *env, jobject thiz,
    jstring jChatId,
    jobjectArray jPlaintextBlocks,
    jstring jMessage,
    jstring jPublicKey) {

    howl::BlockChain::loadSSL();

    char* chatId = const_cast<char *>(env->GetStringUTFChars(jChatId, 0));
    howl::BlockChain* blockChain = new howl::BlockChain(chatId);

    jsize plaintextSize = env->GetArrayLength(jPlaintextBlocks);

    for(jsize i = 0; i < plaintextSize; i++){

        jstring jPlaintextBlock = static_cast<jstring>(
                env->GetObjectArrayElement(jPlaintextBlocks, i));

        char* plaintextBlock = const_cast<char *>(
                env->GetStringUTFChars(jPlaintextBlock, 0));

        blockChain->addPrevSentBlock(plaintextBlock);

        env->ReleaseStringUTFChars(jPlaintextBlock, plaintextBlock);
    }

    char* message = const_cast<char *>(env->GetStringUTFChars(jMessage, 0));
    blockChain->buildSentBlock(message);

    char* publicKey = const_cast<char *>(env->GetStringUTFChars(jPublicKey, 0));

    char* plaintextBlock = blockChain->getLastSentBlock()->toJSON();
    char* encryptedBlock = blockChain->getEncryptedBlock(publicKey);

    jobjectArray jBlocks;

    jBlocks = (jobjectArray)env->NewObjectArray(
            2,
            env->FindClass("java/lang/String"),
            env->NewStringUTF(""));

    env->SetObjectArrayElement(
            jBlocks,
            0,
            env->NewStringUTF(plaintextBlock));

    env->SetObjectArrayElement(
            jBlocks,
            1,
            env->NewStringUTF(encryptedBlock));

    env->ReleaseStringUTFChars(jChatId, chatId);
    env->ReleaseStringUTFChars(jMessage, message);
    env->ReleaseStringUTFChars(jPublicKey, publicKey);
    howl::BlockChain::freeBlockChain(blockChain);

    return jBlocks;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_teamhowl_howl_models_BlockChain_cBuildGenesisBlock(JNIEnv *env, jobject thiz,
    jstring jChatId,
    jstring jPublicKey) {

    howl::BlockChain::loadSSL();

    char* chatId = const_cast<char *>(env->GetStringUTFChars(jChatId, 0));
    char* publicKey = const_cast<char *>(env->GetStringUTFChars(jPublicKey, 0));
    howl::BlockChain* blockChain = new howl::BlockChain(chatId);


    free(chatId);
    blockChain->buildGenisisBlock();
    char* plaintextBlock = blockChain->getLastSentBlock()->toJSON();
    char* encryptedBlock = blockChain->getEncryptedBlock(publicKey);

    jobjectArray jBlocks;

    jBlocks = (jobjectArray)env->NewObjectArray(
            2,
            env->FindClass("java/lang/String"),
            env->NewStringUTF(""));

    env->SetObjectArrayElement(
            jBlocks,
            0,
            env->NewStringUTF(plaintextBlock));

    env->SetObjectArrayElement(
            jBlocks,
            1,
            env->NewStringUTF(encryptedBlock));

    howl::BlockChain::freeBlockChain(blockChain);


    return jBlocks;

}