#include "BookDAO.h"

bool BookDAO::loadAll() {
	KindDAO kinddao;
	ReaderDAO readerdao;
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
		Reader *_Reader = readerdao.searchByName(reader);
		Reader *Reserver = readerdao.searchByName(reserver);
		kind = kinddao.searchByISBN(ISBN);
		Book *p = new Book(id,
					kind,
					_Reader,
					Reserver,
					Date(borrowed),
					Date(reserved),
					Avail );
		_Reader->borrowed.push_back(p);
		Reserver->reserved.push_back(p);
		kind->all.push_back(p);
		all.push_back(p);
	}
	file.close();
	return true;
}

bool BookDAO::saveAll() {
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getBookFileName(), O_WRITE)) return false;
	if (!file.write((void *)&nextID, sizeof(int))) return false;
	for (int i = 0; i < (int)all.size(); ++ i) {
		int id = all[i]->getID();
		if (!file.write((void *)&id, sizeof(int))) return false;
		Kind kind = all[i]->getKind();
		Reader *reader = all[i]->getReader();
		Reader *reserver = all[i]->getReserver();
		if (!file.write((void *)kind.getISBN().c_str(), kind.getISBN().length() * sizeof(char))) return false;
		if (!file.write((void *)reader->getUsername().c_str(), reader->getUsername().length() * sizeof(char))) return false;
		if (!file.write((void *)reserver->getUsername().c_str(), reserver->getUsername().length() * sizeof(char))) return false;
		time_t tmp = all[i]->getBorrowedDate().getTime();
		if (!file.write((void *)&tmp, sizeof(time_t))) return false;
		tmp = all[i]->getReservedDate().getTime();
		if (!file.write((void *)&tmp, sizeof(time_t))) return false;
		if (!file.write((void *)&all[i]->available, sizeof(bool))) return false;
	}
	file.close();
	return true;
}

vector <Book *> BookDAO::getAll() { return all; }
int BookDAO::getNextBookID() { return nextID ++; }

Book *BookDAO::searchByID(int id) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getID() == id)
			return all[i];
	return 0;
}
