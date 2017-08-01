LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS :=-llog

LOCAL_MODULE    := step_dev_bm2260
LOCAL_SRC_FILES := step_dev_bm2260.cpp

include $(BUILD_SHARED_LIBRARY)
