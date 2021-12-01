#include <jni.h>
#include <iostream>

#include "hello.h"
#include "cpphello.h"
#include "chello.h"

JNIEXPORT void JNICALL
Java_HelloWorld_print(JNIEnv *env, jobject obj)
{
    cpphello::Greeter greeter;
    std::cout << greeter.greeting() << std::endl;
    std::cout << cgreeting() << std::endl;
    return;
}