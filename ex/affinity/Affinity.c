#define _GNU_SOURCE

#include <jni.h>
#include <pthread.h>
#include <sched.h>

JNIEXPORT jint JNICALL Java_ex_affinity_Affinity_setAffinity_1impl(JNIEnv *env, jclass obj,
                                                                   jint affinity) {
    cpu_set_t cpuset;
    CPU_ZERO(&cpuset);
    CPU_SET(affinity, &cpuset);
    pthread_t thread = pthread_self();
    int result = pthread_setaffinity_np(thread, sizeof(cpu_set_t), &cpuset);
    if (result != 0) {
        return -1;
    }
    return 0;
}
