/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include<sys/wait.h>
#include<utime.h>
#include <sys/system_properties.h>

#ifndef _DEXOPT
#define _DEXOPT
#ifdef __cplusplus

#ifdef ACDD_DEXOPT_DEBUG
#define  LOG_TAG    "ACDD_DEXOPT_DEBUG"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

#endif

extern "C" {
#endif
#define PROPERTY_KEY_MAX   32
#define PROPERTY_VALUE_MAX  92

#include <sys/system_properties.h>
int ACDD_property_get(const char *key, char *value, const char *default_value)
{
    int len;
    len = __system_property_get(key, value);
    if(len > 0) {
        return len;
    }

    if(default_value) {
        len = strlen(default_value);
        memcpy(value, default_value, len + 1);
    }
    return len;
}
JNIEXPORT void JNICALL Java_org_acdd_dexopt_InitExecutor_dexopt(JNIEnv *, jclass, jstring, jstring,jboolean, jstring);

#ifdef __cplusplus
}
#endif
#endif
