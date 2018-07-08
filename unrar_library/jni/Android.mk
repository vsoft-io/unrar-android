LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

SRC_PATH := unrarsrc

LOCAL_SRC_FILES := \
		$(SRC_PATH)/rar.cpp $(SRC_PATH)/strlist.cpp $(SRC_PATH)/strfn.cpp \
		$(SRC_PATH)/pathfn.cpp $(SRC_PATH)/smallfn.cpp $(SRC_PATH)/global.cpp \
		$(SRC_PATH)/file.cpp $(SRC_PATH)/filefn.cpp $(SRC_PATH)/filcreat.cpp \
		$(SRC_PATH)/archive.cpp $(SRC_PATH)/arcread.cpp $(SRC_PATH)/unicode.cpp \
		$(SRC_PATH)/system.cpp $(SRC_PATH)/isnt.cpp $(SRC_PATH)/crypt.cpp \
		$(SRC_PATH)/crc.cpp $(SRC_PATH)/rawread.cpp $(SRC_PATH)/encname.cpp \
		$(SRC_PATH)/resource.cpp $(SRC_PATH)/match.cpp $(SRC_PATH)/timefn.cpp \
		$(SRC_PATH)/rdwrfn.cpp $(SRC_PATH)/consio.cpp $(SRC_PATH)/options.cpp \
		$(SRC_PATH)/errhnd.cpp $(SRC_PATH)/rarvm.cpp $(SRC_PATH)/secpassword.cpp \
		$(SRC_PATH)/rijndael.cpp $(SRC_PATH)/getbits.cpp $(SRC_PATH)/sha1.cpp \
		$(SRC_PATH)/sha256.cpp $(SRC_PATH)/blake2s.cpp $(SRC_PATH)/hash.cpp \
		$(SRC_PATH)/extinfo.cpp $(SRC_PATH)/extract.cpp $(SRC_PATH)/volume.cpp \
		$(SRC_PATH)/list.cpp $(SRC_PATH)/find.cpp $(SRC_PATH)/unpack.cpp \
		$(SRC_PATH)/headers.cpp $(SRC_PATH)/threadpool.cpp $(SRC_PATH)/rs16.cpp \
		$(SRC_PATH)/cmddata.cpp $(SRC_PATH)/ui.cpp $(SRC_PATH)/filestr.cpp \
		$(SRC_PATH)/recvol.cpp $(SRC_PATH)/rs.cpp $(SRC_PATH)/scantree.cpp \
		$(SRC_PATH)/qopen.cpp

LOCAL_CPPFLAGS := -Wno-logical-op-parentheses -Wno-switch -Wno-dangling-else \
		-DFILE_OFFSET_BITS=64 -DLARGEFILE_SOURCE -DSILENT \
		-fvisibility=hidden -ffunction-sections -fdata-sections

LOCAL_CPP_FEATURES := exceptions

LOCAL_MODULE := unrar

include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
#include $(BUILD_EXECUTABLE)
