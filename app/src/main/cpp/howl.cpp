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
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_teamhowl_howl_utilities_Crypto_stringFromJNI(
    JNIEnv* environment,
    jclass thiz){

    return environment->NewStringUTF(
        "Hello from JNI !Compiled with ABI");
}

