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

extern "C"
JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_utilities_Crypto_stringFromJNI(JNIEnv *env, jclass clazz) {

    char* userAPublic = NULL;
    char* userAPrivate = NULL;

    howl::BlockChain::loadSSL();
    howl::BlockChain::generateKeyPair(&userAPublic, &userAPrivate);

    return env->NewStringUTF(userAPublic);//userAPublic);
}