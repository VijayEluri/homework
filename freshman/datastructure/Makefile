MAIN=main
TEST=test
SRC:=$(shell ls -A |sed -e "s/[^.]\+\.tar\.gz//g" )
BOOK=BEES
$(BOOK).pdf: $(BOOK).tex
	xelatex $(BOOK).tex
	bibtex $(BOOK)||true
	xelatex $(BOOK).tex
	xelatex $(BOOK).tex
all: $(MAIN) $(BOOK).pdf
$(MAIN): $(MAIN).cpp
	g++ -O2 $(MAIN).cpp -o $(MAIN) -Wall -std=c++0x
compile:
	g++ -O2 $(MAIN).cpp -o $(MAIN) -Wall
run:
	./$(MAIN)
debug:
	g++ -g $(MAIN).cpp -o $(MAIN) -Wall
	gdb $(MAIN)
prof:
	g++ -g -pg $(MAIN).cpp -o $(MAIN) -Wall
	gprof $(MAIN)
clean:
	rm -rf $(MAIN)
	rm -f *.aux *.toc *.lon *.lor *.lof *.ilg *.idx *.ind *.out *.log *.exa
tarball: clean
	rm -rf BEES.tar.gz
	tar -czvf BEES.tar.gz $(SRC)
test:
	g++ -g $(MAIN).cpp -o $(MAIN) -Wall
	./$(MAIN)
