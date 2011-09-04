program = Bashoid

.PHONY: build
.PHONY: clean
.PHONY: runa

build:
	ant compile && ant jar

clean:
	ant clean

run:
	ant run
