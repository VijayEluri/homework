// This is ***just an example***.
// Author: Xiao Jia
// Date: 2010/12/02

#include "ILibrary.h"
#include "Library.h"
#include "Reader.h"

int main() {
	ILibrary *lib = new Library();
	lib->initialize();
	
	Reader *reader = lib->readerLogin("5100309000", "secret");
	if (reader) {
		// do something with reader
	}
	
	lib->finalize();
	delete lib;
	
	return 0;
}
