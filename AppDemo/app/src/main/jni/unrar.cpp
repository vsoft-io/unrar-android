#include <jni.h>
#include <time.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define MAX_ARGC 256

extern int main(int argc, const char *argv[]);

static jint unrar(JNIEnv *env, jobject obj, jobjectArray array)
{
	int ret;
	char tmp[256];
	jsize size = env->GetArrayLength(array);
	jstring string[size];

	const char *argv[MAX_ARGC];
	const char *opt[MAX_ARGC];
	int argc = 0, i = 0;

	for (i = 0; i < size; i++) {
		string[i] = (jstring) env->GetObjectArrayElement(array, i);
		opt[i] = env->GetStringUTFChars(string[i], 0);
	}

	for (i = 0; i < size; i++) {
		argv[argc++] = opt[i];
	}
	argv[argc] = 0;

	ret = main(argc, argv);

	for (i = 0; i < size; i++) {
		env->ReleaseStringUTFChars(string[i], opt[i]);
		env->DeleteLocalRef(string[i]);
	}
	return ret;
}

static JNINativeMethod gMethods[] = {
	{
		"unrar",
		"([Ljava/lang/String;)I",
		(void *) unrar
	},
};

static int jniRegisterNativeMethods(JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods)
{
	jclass c = env->FindClass(className);
	if (c == NULL) {
		return JNI_ERR;
	}
	if (env->RegisterNatives(c, gMethods, numMethods) < 0) {
		return JNI_ERR;
	}

	return JNI_OK;
}

static int registerNatives(JNIEnv* env)
{
	return jniRegisterNativeMethods(env, "com/rarlab/unrar/Unrar", \
		gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv* env = NULL;
	jint result = JNI_ERR;

	if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
		goto bail;
	}

	if (registerNatives(env) < 0) {
		goto bail;
	}

	result = JNI_VERSION_1_6;

bail:
	return result;
}
