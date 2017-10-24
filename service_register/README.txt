
INFORMATION:

	Bonjour Android, it's Zeroconf:
		https://medium.com/@_tiwiz/bonjour-android-its-zeroconf-8e3d3fde760e

	Connect RPI w/ Eithernet cable: 
		https://stackoverflow.com/questions/16040128/hook-up-raspberry-pi-via-ethernet-to-laptop-without-router

	JmDNS connection lost problem:
		https://github.com/jmdns/jmdns/issues/19 




The present file is run with:

java -Dgui=false -jar dist/service_register-1.0.jar -gui



About gRPC:
	First, dowload it, uncompress it and cd into it
	./configure
	make
	make check
	sudo make install
	sudo ldconfig #this one refreshes the shared library cache

	check the generated "src/protoc" file
	
	now, open ./java project into NetBeans and build it
	
