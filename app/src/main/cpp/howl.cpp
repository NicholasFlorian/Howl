
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
    jstring local_user_id,
    jstring foreign_user_id) {

    char* chatId;
    char* localId;
    char* foreignId;
    jstring jChatId;

    localId = const_cast<char *>(env->GetStringUTFChars(local_user_id, 0));
    foreignId = const_cast<char *>(env->GetStringUTFChars(foreign_user_id, 0));

    howl::BlockChain::generateChatId(&chatId, localId, foreignId);
    jChatId = env->NewStringUTF(chatId);

    free(chatId);
    env->ReleaseStringUTFChars(local_user_id, localId);
    env->ReleaseStringUTFChars(local_user_id, localId);

    return jChatId;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_utilities_Crypto_generateUserId(JNIEnv *env, jclass clazz,
    jstring local_address) {

    char* localAddress;
    char* userId;
    jstring jUserId;

    localAddress = const_cast<char *>(env->GetStringUTFChars(local_address, 0));

    howl::BlockChain::generateUserId(&userId, localAddress);
    jUserId = env->NewStringUTF(userId);

    free(userId);
    env->ReleaseStringUTFChars(local_address, localAddress);

    return jUserId;
}

/** BlockChain Java Library */
extern "C"
JNIEXPORT jlong JNICALL
Java_com_teamhowl_howl_models_BlockChain_buildBlockChain(JNIEnv *env, jobject thiz,
    jstring chat_id) {

    char* chatId;
    howl::BlockChain* blockChain;

    chatId = const_cast<char *>(env->GetStringUTFChars(chat_id, 0));

    blockChain = new howl::BlockChain(chatId);

    env->ReleaseStringUTFChars(chat_id, chatId);

    return (long) blockChain;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_models_BlockChain_buildGenesisBlock(JNIEnv *env, jobject thiz,
    jobject pointer) {

    howl::BlockChain* blockChain;

    blockChain = reinterpret_cast<howl::BlockChain *>(pointer);
    blockChain->buildGenisisBlock();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_teamhowl_howl_models_BlockChain_buildSentBlock(JNIEnv *env, jobject thiz,
    jlong pointer,
    jstring plain_text) {

    howl::BlockChain* blockChain;
    char* plainText;

    blockChain = reinterpret_cast<howl::BlockChain *>(pointer);
    plainText = const_cast<char *>(env->GetStringUTFChars(plain_text, 0));

    blockChain->buildSentBlock(plainText);

    env->ReleaseStringUTFChars(plain_text, plainText);

    return;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_models_BlockChain_addReceivedBlock(JNIEnv *env, jobject thiz,
    jlong pointer,
    jstring encrypted_block,
    jstring public_key) {

    howl::BlockChain* blockChain;
    char* encryptedBlock;
    char* publicKey;
    char* plainTextBlock;
    jstring jPlainTextBlock;

    blockChain = reinterpret_cast<howl::BlockChain *>(pointer);
    encryptedBlock = const_cast<char *>(env->GetStringUTFChars(encrypted_block, 0));
    publicKey = const_cast<char *>(env->GetStringUTFChars(public_key, 0));

    blockChain->addReceivedBlock(encryptedBlock, publicKey);
    plainTextBlock = blockChain->getLastReceivedBlock()->toString();

    jPlainTextBlock = env->NewStringUTF(plainTextBlock);

    env->ReleaseStringUTFChars(encrypted_block, encryptedBlock);
    env->ReleaseStringUTFChars(public_key, publicKey);
    free(plainTextBlock);

    return jPlainTextBlock;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_models_BlockChain_addPrevSentBlock(JNIEnv *env, jobject thiz,
    jlong pointer,
    jstring encrypted_block,
    jstring public_key) {

    howl::BlockChain* blockChain;
    char* encryptedBlock;
    char* publicKey;
    char* plainTextBlock;
    jstring jPlainTextBlock;

    blockChain = reinterpret_cast<howl::BlockChain *>(pointer);
    encryptedBlock = const_cast<char *>(env->GetStringUTFChars(encrypted_block, 0));
    publicKey = const_cast<char *>(env->GetStringUTFChars(public_key, 0));

    blockChain->addPrevSentBlock(encryptedBlock, publicKey);
    plainTextBlock = blockChain->getLastSentBlock()->toString();

    jPlainTextBlock = env->NewStringUTF(plainTextBlock);

    env->ReleaseStringUTFChars(encrypted_block, encryptedBlock);
    env->ReleaseStringUTFChars(public_key, publicKey);
    free(plainTextBlock);

    return jPlainTextBlock;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_models_BlockChain_getEncryptedBlock(JNIEnv *env, jobject thiz,
    jlong pointer,
    jstring private_key) {

    howl::BlockChain* blockChain;
    char* encryptedBlock;
    char* privateKey;
    jstring jEncryptedBlock;

    blockChain = reinterpret_cast<howl::BlockChain *>(pointer);
    privateKey = const_cast<char *>(env->GetStringUTFChars(private_key, 0));

    encryptedBlock = blockChain->getEncryptedBlock(privateKey);
    jEncryptedBlock = env->NewStringUTF(encryptedBlock);

    env->ReleaseStringUTFChars(private_key, privateKey);

    return jEncryptedBlock;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_teamhowl_howl_models_BlockChain_cleanup(JNIEnv *env, jobject thiz,
    jlong pointer) {


}
