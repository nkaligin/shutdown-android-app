#!/bin/bash
adb shell mount -o rw,remount -t yaffs2 /dev/block/mtdblock03 /system
adb push ./system/bin/su /system/xbin/su
adb shell chmod 06755 /system
adb shell chmod 06755 /system/xbin/su
adb install ./system/app/Superuser.apk
