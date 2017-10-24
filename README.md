# Java-Service-Discovery
These are a group of applications (client and server) with gRPC and Network libraries. Made to test out concepts for UnitLabs

How to run files:
    java -Dgui=false -jar service_discovery/target/service_discovery-1.0.2-SNAPSHOT-jar-with-dependencies.jar -gui
    java -Dgui=false -jar service_register/target/service_register-1.0.3-SNAPSHOT-jar-with-dependencies.jar -gui

Simple gRPC client and servers can be used standalone. java -jar jar_bottle.jar

Building and Running:

    1. You need to check if all dependencies are met
        1.1. service_discovery and service_register have JmDNS and GetNetworkInterface (add from folder) as dependencies
    2. all can be built with maven, but I use NetBeans... for now at least...

Known Bugs:
    1. Internal service representation needs to be re-written
    2. Whole code needs some re-factoring and perhaps even re-drawing

Unknown Bugs (yup.. I think I've spotted something!):
    1. Does the interface name on the question is the right one? I got confused because wire interface gave me wrong IP... is it still like that? Anyway, that portion of code could be re-tweaked.

Test discovery: Raspberry Pi <-> Laptop/PC

    STEPS TO REPRODUCE

	1. Install RPI and 'touch ssh'

	2. Whait for it to go up and
		$ ping raspberrypi.local

	3. Grab ip 169.254.181.88

	4. ssh into it or ftp and put the service_register.jar there

	5. Setup wlan in RPI
	   (Docs: https://www.raspberrypi.org/documentation/configuration/wireless/wireless-cli.md )

		a) get wireless networks
			$ sudo iwlist wlan0 scan

		b) edit sudo nano /etc/wpa_supplicant/wpa_supplicant.conf
			$ sudo nano /etc/wpa_supplicant/wpa_supplicant.conf

		c) $ wpa_passphrase "Max Interwebs" "airborne"
		network={
			ssid="Max Interwebs"
			#psk="airborne"
			psk=750eb3bfac99df5eacd9ebe3704b447319162c8d759fe0c10fd7aebdd083a1a5
		}

		d) add in the bottom:
			network={
				ssid="Max Interwebs"
				psk=750eb3bfac99df5eacd9ebe3704b447319162c8d759fe0c10fd7aebdd083a1a5
			}

		e) wpa_cli -i wlan0 reconfigure



	6. Install java into RPI (check if it has internet and update by the way)
		sudo apt-get update && sudo apt-get install oracle-java8-jdk
		test java: java -version

	7. initialize service_register.jar:
		java -jar service_register-1.0.3-SNAPSHOT-jar-with-dependencies.jar

About gRPC:
	First, dowload it, uncompress it and cd into it
	./configure
	make
	make check
	sudo make install
	sudo ldconfig #this one refreshes the shared library cache

	check the generated "src/protoc" file

	now, open ./java project into NetBeans and build it



RESOURCES & INFORMATION:

	Bonjour Android, it's Zeroconf:
		https://medium.com/@_tiwiz/bonjour-android-its-zeroconf-8e3d3fde760e

	Connect RPI w/ Eithernet cable:
		https://stackoverflow.com/questions/16040128/hook-up-raspberry-pi-via-ethernet-to-laptop-without-router

	JmDNS connection lost problem:
		https://github.com/jmdns/jmdns/issues/19


    Other stuff:

        1. Setup wireless network on RPI (the official way :) )
            https://www.raspberrypi.org/documentation/configuration/wireless/wireless-cli.md
        2. ProtocolLabs: https://protocol.ai/ and their libs: https://libp2p.io (https://github.com/libp2p/js-libp2p-mdns)
        3. Mathia's blog, article on modularity, that shows the use case of mDNS http://mafintosh.com/pragmatic-modularity.html
        4. Watson's work on top of Mathia's https://github.com/watson/bonjour
        5. BBC MediaScape http://www.bbc.co.uk/rd/projects/mediascape
        6. Service "Disco" https://github.com/bbc/service-disco - Apache license 2.0
        7. Java Multicast DNS (lower level then JmDNS): https://github.com/posicks/mdnsjava (originally created for the Society of Motion Picture and Television Engineers (SMPTE))
