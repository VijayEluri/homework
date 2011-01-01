#include "BookDAO.h"

static bool BookDAO::loadAll() {
	KindDAO kinddao;
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getBookFileName(), O_READ)) return false;
	void *token = 0;
	int id;
	string ISBN, reader, reserver;
	int borrowed, reserved;
	Kind *kind;
	bool Avail;
	if (!(token = file.read())) return false;
	nextID = *((int *)token);
	free(token);
	while (token = file.read()) {
		id = *((int *)token);
		free(token);
		if (!(token = file.read())) return false;
		ISBN = (char *)token;
		free(token);
		if (!(token = file.read())) return false;
		reader = (char *)token;
		free(token);
		if (!(token = file.read())) return false;
		reserver = (char *)token;
		free(token);
		if (!(token = file.read())) return false;
		borrowed = *((int *)token);
		free(token);
		if (!(token = file.read())) return false;
		reserved = *((int *)token);
		free(token);
		if (!(token = file.read())) return false;
		Avail = *((bool *)token);
		free(token);
		//TO DO
	}
}
