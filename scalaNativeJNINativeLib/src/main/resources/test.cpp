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

struct jdbb {
    uint8_t* ptr;
    int len;
};

extern "C" {
    int strlen16(const char16_t* strarg)
    {
       int count = 0;
       if(!strarg)
         return -1; //strarg is NULL pointer
       char16_t* str = (char16_t*) strarg;
       while(*str)
       {
          count++;
          str++;
       }
       return count;
    }

    jclass JNIEnv_FindClass(JNIEnv *thiz, const char *name) {
//        fprintf(stderr, "finding class %s\n", name);
        return (jclass) 5;
    }

    jobject JNIEnv_NewGlobalRef(JNIEnv *env, jobject lobj) {
//        fprintf(stderr, "NEW GLOBAL REF\n");
        return (jobject) 7;
    }

    void JNIEnv_DeleteLocalRef(JNIEnv *env, jobject lobj) {
//        fprintf(stderr, "DELETE LOCAL REF\n");
    }

    void JNIEnv_DeleteGlobalRef(JNIEnv *env, jobject lobj) {
//        fprintf(stderr, "DELETE GLOBAL REF\n");
    }

    jobject JNIEnv_NewObject(JNIEnv *thiz, jclass cls, jmethodID constructor, ...) {
        fprintf(stderr, "in newObjectV!\n");
//        char* arg = va_arg(args, char*);
//        fprintf(stderr, "")
        return NULL;
    }

    jint JNIEnv_ThrowNew(JNIEnv *env, jclass clazz, const char *msg) {
//        fprintf(stderr, "HTORLKWJ\n");
        return 0;
    }

    jmethodID JNIEnv_GetMethodID(JNIEnv *env, jclass clazz, const char *name, const char *sig) {
//        fprintf(stderr, "GETMETHODID!\n");
        return (jmethodID) 5;
    }

    jobject JNIEnv_NewDirectByteBuffer(JNIEnv* env, void* address, jlong capacity) {
        jdbb* created = (jdbb*) malloc(sizeof(jdbb));
        created->ptr = (uint8_t*) address;
        created->len = (int) capacity;
        return (jobject) created;
    }

    void* JNIEnv_GetDirectBufferAddress(JNIEnv* env, jobject buf) {
        jdbb* actual = (jdbb*) buf;
        return (void*) actual->ptr;
    }

    jlong JNIEnv_GetDirectBufferCapacity(JNIEnv* env, jobject buf) {
        jdbb* actual = (jdbb*) buf;
        return (jlong) actual->len;
    }

    JNIEnv* createEnv(jstring (JNICALL *JNIEnv_NewString) (JNIEnv *env, const jchar *unicode, jsize len),
                      jsize (JNICALL *JNIEnv_GetStringLength) (JNIEnv *env, jstring str),
                      const jchar * (JNICALL *JNIEnv_GetStringCritical) (JNIEnv *env, jstring string, jboolean *isCopy),
                      void (JNICALL *JNIEnv_ReleaseStringCritical) (JNIEnv *env, jstring string, const jchar *cstring),

                      void (JNICALL *JNIEnv_SetShortArrayRegion) (JNIEnv *env, jshortArray array, jsize start, jsize len, const jshort *buf),
                      void (JNICALL *JNIEnv_SetFloatArrayRegion) (JNIEnv *env, jfloatArray array, jsize start, jsize len, const jfloat *buf),
                      jsize (JNICALL *JNIEnv_GetArrayLength) (JNIEnv *env, jarray array)) {
        struct JNINativeInterface_ * env = (struct JNINativeInterface_ *) malloc(sizeof(struct JNINativeInterface_));
        env->FindClass = JNIEnv_FindClass;
        env->NewGlobalRef = JNIEnv_NewGlobalRef;
        env->DeleteGlobalRef = JNIEnv_DeleteGlobalRef;
        env->DeleteLocalRef = JNIEnv_DeleteLocalRef;
        env->NewObject = JNIEnv_NewObject;
        env->ThrowNew = JNIEnv_ThrowNew;
        env->GetMethodID = JNIEnv_GetMethodID;

        env->NewDirectByteBuffer = JNIEnv_NewDirectByteBuffer;
        env->GetDirectBufferAddress = JNIEnv_GetDirectBufferAddress;
        env->GetDirectBufferCapacity = JNIEnv_GetDirectBufferCapacity;

        env->NewString = JNIEnv_NewString;
        env->GetStringLength = JNIEnv_GetStringLength;
        env->GetStringCritical = JNIEnv_GetStringCritical;
        env->ReleaseStringCritical = JNIEnv_ReleaseStringCritical;

        env->SetShortArrayRegion = JNIEnv_SetShortArrayRegion;
        env->SetFloatArrayRegion = JNIEnv_SetFloatArrayRegion;
        env->GetArrayLength = JNIEnv_GetArrayLength;

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

    void testVM(JavaVM *vm, JNIEnv* env) {
        printf("initing\n");
        //Java_edu_wpi_first_wpilibj_hal_PWMJNI_initializePWMPort(env, NULL, (jint) 33554688);
    }
}
