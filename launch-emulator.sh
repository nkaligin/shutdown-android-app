#!/bin/bash
emulator -avd android-2.2 -partition-size 256 -netspeed full -netdelay none &
echo "Launched emulator with pid: "
ps ax | grep [e]mulator
