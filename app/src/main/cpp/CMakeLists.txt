
# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Set Version
cmake_minimum_required(VERSION 3.18.1)

# Set the project name for Java class loading
project("howl")

# Set the construction of the library
add_library(howl SHARED block.cpp blockChain.cpp howl.cpp )

# Local Location of OpenSSL C++ directorires
set(SSL_PATH /Users/nick/Documents/OpenSSL/openssl-3.0.1)

# Include files for OpenSSL
include_directories(${SSL_PATH}/include)
include_directories(${SSL_PATH}/include/crypto)
include_directories(${SSL_PATH}/include/internal)
include_directories(${SSL_PATH}/include/openssl)

# Include the OpenSSL libraries
set(open-ssl-libs ${SSL_PATH}/libssl.a ${SSL_PATH}/libcrypto.a)

# Include the logging library
find_library(log-lib log)

# Link our libraries to our project
target_link_libraries(howl ${open-ssl-libs} ${log-lib} )