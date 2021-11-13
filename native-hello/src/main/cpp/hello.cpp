#include <jni.h>
#include <iostream>

#include "hello.h"
#include "demo.h"

JNIEXPORT void JNICALL
Java_HelloWorld_print(JNIEnv *env, jobject obj)
{
    demo::Greeter greeter;
    std::cout << greeter.greeting() << std::endl;
    return;
}