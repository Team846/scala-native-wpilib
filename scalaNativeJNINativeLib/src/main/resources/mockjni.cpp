#include <jni.h>
#include <stdlib.h>
#include <malloc.h>
#include <stdio.h>
#include <cstdarg>
#include <support/jni_util.h>
#include <HALUtil.h>
#include <wchar.h>

using namespace frc;
using namespace wpi::java;

extern "C" {
    JNIEnv* createEnv(jclass (JNICALL *JNIEnv_FindClass) (JNIEnv *env, const char *name),
                      jobject (JNICALL *JNIEnv_NewGlobalRef) (JNIEnv *env, jobject lobj),
                      void (JNICALL *JNIEnv_DeleteLocalRef) (JNIEnv *env, jobject obj),
                      void (JNICALL *JNIEnv_DeleteGlobalRef) (JNIEnv *env, jobject gref),
                      jobject (JNICALL *JNIEnv_NewObjectV) (JNIEnv *env, jclass clazz, jmethodID methodID, va_list args),
                      jmethodID (JNICALL *JNIEnv_GetMethodID) (JNIEnv *env, jclass clazz, const char *name, const char *sig),

                      jint (JNICALL *JNIEnv_Throw) (JNIEnv *env, jthrowable obj),
                      jint (JNICALL *JNIEnv_ThrowNew) (JNIEnv *env, jclass clazz, const char *msg),

                      jstring (JNICALL *JNIEnv_NewString) (JNIEnv *env, const jchar *unicode, jsize len),
                      jsize (JNICALL *JNIEnv_GetStringLength) (JNIEnv *env, jstring str),
                      const jchar * (JNICALL *JNIEnv_GetStringCritical) (JNIEnv *env, jstring string, jboolean *isCopy),
                      void (JNICALL *JNIEnv_ReleaseStringCritical) (JNIEnv *env, jstring string, const jchar *cstring),

                      void (JNICALL *JNIEnv_SetShortArrayRegion) (JNIEnv *env, jshortArray array, jsize start, jsize len, const jshort *buf),
                      void (JNICALL *JNIEnv_SetFloatArrayRegion) (JNIEnv *env, jfloatArray array, jsize start, jsize len, const jfloat *buf),
                      jsize (JNICALL *JNIEnv_GetArrayLength) (JNIEnv *env, jarray array),

                      jobject (JNICALL *JNIEnv_NewDirectByteBuffer) (JNIEnv* env, void* address, jlong capacity),
                      void* (JNICALL *JNIEnv_GetDirectBufferAddress) (JNIEnv* env, jobject buf),
                      jlong (JNICALL *JNIEnv_GetDirectBufferCapacity) (JNIEnv* env, jobject buf)) {
        struct JNINativeInterface_ * env = (struct JNINativeInterface_ *) malloc(sizeof(struct JNINativeInterface_));
        env->FindClass = JNIEnv_FindClass;
        env->NewGlobalRef = JNIEnv_NewGlobalRef;
        env->DeleteLocalRef = JNIEnv_DeleteLocalRef;
        env->DeleteGlobalRef = JNIEnv_DeleteGlobalRef;
        env->NewObjectV = JNIEnv_NewObjectV;
        env->GetMethodID = JNIEnv_GetMethodID;

        env->Throw = JNIEnv_Throw;
        env->ThrowNew = JNIEnv_ThrowNew;

        env->NewString = JNIEnv_NewString;
        env->GetStringLength = JNIEnv_GetStringLength;
        env->GetStringCritical = JNIEnv_GetStringCritical;
        env->ReleaseStringCritical = JNIEnv_ReleaseStringCritical;

        env->SetShortArrayRegion = JNIEnv_SetShortArrayRegion;
        env->SetFloatArrayRegion = JNIEnv_SetFloatArrayRegion;
        env->GetArrayLength = JNIEnv_GetArrayLength;

        env->NewDirectByteBuffer = JNIEnv_NewDirectByteBuffer;
        env->GetDirectBufferAddress = JNIEnv_GetDirectBufferAddress;
        env->GetDirectBufferCapacity = JNIEnv_GetDirectBufferCapacity;

        void** ptr = (void**) malloc(sizeof(void*));
        *ptr = env;

        return (JNIEnv*) ptr;
    }

    jint JavaVM_GetEnv(JavaVM *thiz, void **penv, jint version) {
        *penv = ((JNIInvokeInterface_ *) *((void**) thiz))->reserved0;
        return JNI_OK;
    }

    JavaVM* createVM(JNIEnv* env) {
        struct JNIInvokeInterface_ * vm = (struct JNIInvokeInterface_ *) malloc(sizeof(struct JNIInvokeInterface_));

        vm->reserved0 = env; // we use reserved0 to store the env
        vm->GetEnv = JavaVM_GetEnv;

        void** ptr = (void**) malloc(sizeof(void*));
        *ptr = vm;

        return (JavaVM*) ptr;
    }

    void testVM(JavaVM *vm, JNIEnv* env, jobject buf) {
        printf("hello!\n");
//        env->NewObject((jclass) 5, (jmethodID) 5, "foobar");
        JException foo = JException(env, "java/lang/RuntimeException");
        printf("created exception!\n");
        foo.Throw(env, "hallo");

//        printf("initing %p %lld\n", env->GetDirectBufferAddress(buf), env->GetDirectBufferCapacity(buf));
//        uint8_t *data = (uint8_t *) env->GetDirectBufferAddress(buf);
//        data[0] = 5;
//        data[1] = 7;
        //Java_edu_wpi_first_wpilibj_hal_PWMJNI_initializePWMPort(env, NULL, (jint) 33554688);
    }
}
