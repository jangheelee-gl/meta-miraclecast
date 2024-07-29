DESCRIPTION = "Software to connect external monitors to your system via Wi-Fi."
HOMEPAGE = "https://github.com/albfan/miraclecast"

LICENSE = "LGPLv2.1 & GPLv2"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=7314adf793af1c4ce355355a659e6891 \
    file://LICENSE_htable;md5=2d5025d4aa3495befef8f17206a5b0a1 \
    file://LICENSE_gdhcp;md5=42c8401a5a2eda9c8e4419921d4e8559 \
    file://LICENSE_lgpl;md5=4fbd65380cdd255951079008b364516c \
"


SRC_URI = "git://github.com/jangheelee-gl/miraclecast.git;branch=geeksloft"
SRCREV = "28d6e010d856c63cca0644af99c67e369f279821"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/build"

inherit cmake autotools pkgconfig

#EXTRA_OECONF = ""
EXTRA_OECONF = "--host=${HOST_SYS} --build=${BUILD_SYS} --target=${TARGET_SYS}"
EXTRA_OECMAKE += "-DREADLINE_INCLUDE_DIR=${STAGING_INCDIR}/readline -DREADLINE_LIBRARY=${STAGING_LIBDIR}/libreadline.so"


DEPENDS = "cmake glib-2.0 gstreamer1.0 systemd readline"

RDEPENDS_${PN} += " \
    bash \
    iproute2 \
    gstreamer1.0-libav \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    python3-core \
"


do_configure_prepend() {
	bbnote "STAGING_INCDIR: ${STAGING_INCDIR}"
	bbnote "STAGING_LIBDIR: ${STAGING_LIBDIR}"
	export READLINE_INCLUDE_DIR=${STAGING_INCDIR}/readline
	export READLINE_LIBRARY=${STAGING_LIBDIR}/libreadline.so
	bbnote "READLINE_INCLUDE_DIR: ${READLINE_INCLUDE_DIR}"
	bbnote "READLINE_LIBRARY: ${READLINE_LIBRARY}"
	cd ${S}
	mkdir build
	cd build
 	cmake -DCMAKE_INSTALL_PREFIX=/usr -DREADLINE_INCLUDE_DIR=${STAGING_INCDIR}/readline -DREADLINE_LIBRARY=${STAGING_LIBDIR}/libreadline.so ..
	
}

do_compile() {
	cd ${S}/build
	bbnote "COMPILE_WORKING_DIR: ${S}/build"
	oe_runmake
}

do_install() {
	cd ${S}/build
	bbnote "BUILD_DIR: ${S}/build"
	bbnote "DEST_DIR: ${D}"
	oe_runmake install DESTDIR=${D}
	install -d ${D}${base_bindir}
	ln -s ${D}${base_sbindir}/ip  ${D}${base_bindir}/ip
}

do_install_append() {
    install -Dm 0644 ${S}/res/org.freedesktop.miracle.conf ${D}${sysconfdir}/dbus-1/system.d/org.freedesktop.miracle.conf
    install -Dm 0755 ${S}/res/kill-wpa.sh ${D}${bindir}/miraclecast/kill-wpa.sh
    install -Dm 0755 ${S}/res/miracle-utils.sh ${D}${bindir}/miraclecast/miracle-utils.sh
    install -Dm 0755 ${S}/res/normal-wifi.sh ${D}${bindir}/miraclecast/normal-wifi.sh
    install -Dm 0755 ${S}/res/show_wpa.sh ${D}${bindir}/miraclecast/show_wpa.sh
    install -Dm 0755 ${S}/res/test-hardware-capabilities.sh ${D}${bindir}/miraclecast/test-hardware-capabilities.sh
    install -Dm 0755 ${S}/res/test-viewer.sh ${D}${bindir}/miraclecast/test-viewer.sh
    install -Dm 0755 ${S}/res/write-udev-rule.sh ${D}${bindir}/miraclecast/write-udev-rule.sh
    bbnote "BIN_DIR: ${bindir}"    
    install -Dm 0755 ${S}/src/wifi/wifid-supplicant.c  ${D}${bindir}/miraclecast/wifid-supplicant.c
}

FILES_${PN} += "${bindir}/* ${libdir}/* ${includedir}/*"

