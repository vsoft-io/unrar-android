LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := unrar_libs/$(TARGET_ARCH_ABI)/libunrar.so

LOCAL_MODULE := unrar-prebuilt

include $(PREBUILT_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_SRC_FILES := unrar.cpp

LOCAL_SHARED_LIBRARIES := unrar-prebuilt

LOCAL_CPPFLAGS := -fvisibility=hidden -ffunction-sections -fdata-sections

LOCAL_MODULE := unrar-jni

include $(BUILD_SHARED_LIBRARY)
