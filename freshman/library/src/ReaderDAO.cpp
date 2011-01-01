#include "ReaderDAO.h"
#include "System.h"

bool ReaderDAO::loadAll() {
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getReaderFileName(), O_READ)) return false;
	char *token;
	string username, password;
	return true;
}

bool ReaderDAO::saveAll() {
	return false;
}
