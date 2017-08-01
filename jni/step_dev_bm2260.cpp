#include <jni.h>

#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <android/log.h>

#define TAG "hcj.StepsLife"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL
Java_com_wt_health_StepDevice_nativeCmd(JNIEnv* env, jobject obj, jint cmd, jint param);
#ifdef __cplusplus
}
#endif

jint Java_com_wt_health_StepDevice_nativeCmd(JNIEnv* env, jobject obj, jint cmd, jint param){
	char dev_name[128];
	int dev = -1;
	int count;

	strcpy(dev_name, "/dev/bm2260m_dev");
	dev = open(dev_name, O_WRONLY);
	if (dev < 0)  {
		LOGI("error open dev=%d",dev);
		return 0;
	}
	LOGI("open dev=%d",dev);
	ioctl(dev,cmd,param);

	if (close(dev) != 0) {
		LOGI("close dev error");
		return 0;
	}
}
