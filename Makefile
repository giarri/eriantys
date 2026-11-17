SHELL := /bin/bash

UNAME_S := $(shell uname -s)
UNAME_M := $(shell uname -m)

# If JAVA_HOME is unset or stale, fall back to the platform's JDK 17 install so
# every target works out of the box.
ifeq (,$(wildcard $(JAVA_HOME)/bin/java))
ifeq ($(UNAME_S),Darwin)
export JAVA_HOME := $(shell brew --prefix openjdk@17 2>/dev/null)/libexec/openjdk.jdk/Contents/Home
else
export JAVA_HOME := /usr/lib/jvm/java-17-openjdk
endif
endif

ifeq ($(UNAME_S),Darwin)
export ANDROID_HOME ?= /opt/homebrew/share/android-commandlinetools
else
export ANDROID_HOME ?= $(HOME)/Android/Sdk
endif
ADB := $(ANDROID_HOME)/platform-tools/adb
EMULATOR := $(ANDROID_HOME)/emulator/emulator
AVDMANAGER := $(ANDROID_HOME)/cmdline-tools/latest/bin/avdmanager

AVD_NAME ?= eriantys
ifneq (,$(filter arm64 aarch64,$(UNAME_M)))
AVD_ABI := arm64-v8a
else
AVD_ABI := x86_64
endif

DESKTOP_JAR = $$(ls desktop/build/libs/desktop-*-all.jar | head -1)

.PHONY: help server desktop android avd emulator ip devices clean

help:
	@echo "Targets:"
	@echo "  make server           Build (if needed) and run the game server (port 12345, override with ERIANTYS_PORT=<n>)"
	@echo "  make desktop          Build (if needed) and run the desktop GUI client"
	@echo "  make android          Build the debug APK, install it and launch it on the first connected device/emulator"
	@echo "  make avd              Create a local emulator (once) named '$(AVD_NAME)'"
	@echo "  make emulator         Boot the '$(AVD_NAME)' emulator"
	@echo "  make devices          List adb devices/emulators currently visible"
	@echo "  make ip               Print this machine's LAN IP (enter it as 'Server address' on a physical device)"
	@echo "  make clean            ./gradlew clean"
	@echo ""
	@echo "Emulator connects to the server via 10.0.2.2; a physical phone needs 'make ip' instead."

server:
	./gradlew :desktop:shadowJar -q
	java -jar $(DESKTOP_JAR) server

desktop:
	./gradlew :desktop:shadowJar -q
	java -jar $(DESKTOP_JAR) gc

android:
	./gradlew :android:assembleDebug -q
	$(ADB) install -r android/build/outputs/apk/debug/android-debug.apk
	$(ADB) shell am start -n it.polimi.ingsw.android/.MainActivity

avd:
	@if $(AVDMANAGER) list avd | grep -q "Name: $(AVD_NAME)$$"; then \
		echo "AVD '$(AVD_NAME)' already exists"; \
	else \
		echo no | $(AVDMANAGER) create avd -n $(AVD_NAME) -k "system-images;android-35;google_apis;$(AVD_ABI)" -d pixel_6; \
	fi

emulator:
	$(EMULATOR) -avd $(AVD_NAME)

devices:
	$(ADB) devices -l

ip:
ifeq ($(UNAME_S),Darwin)
	@ipconfig getifaddr en0 2>/dev/null || ipconfig getifaddr en1 2>/dev/null || echo "No active network interface found"
else
	@ip -4 route get 1.1.1.1 2>/dev/null | awk '{for (i = 1; i < NF; i++) if ($$i == "src") print $$(i + 1)}' | grep . || echo "No active network interface found"
endif

clean:
	./gradlew clean
