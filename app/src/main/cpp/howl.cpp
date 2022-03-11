// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("howl");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("howl")
//      }
//    }

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
    char* publicKey = NULL;
    char* privateKey = NULL;

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