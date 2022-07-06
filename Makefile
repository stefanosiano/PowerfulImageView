.PHONY: all clean build dryRelease update stop checkFormat format api assembleBenchmarkTestRelease assembleUiTestRelease

all: stop clean createNonRsModule build
assembleLibrary: stop clean createNonRsModule assembleLibraryModules
publish: stop publishLibrary

# deep clean
clean:
	./gradlew clean

# build and run tests
build:
	./gradlew build

# We stop gradle to make sure there are no locked files
stop:
	./gradlew --stop

# We copy the changes done to the rs module to the non_rs one
createNonRsModule:
	./gradlew createNonRs

# Assemble release of the library modules to publish
assembleLibraryModules:
	./gradlew :powerfulimageview:assembleRelease
	./gradlew :powerfulimageview_rs:assembleRelease

# Publish library to Sonatype
publishLibrary:
	./gradlew publish
