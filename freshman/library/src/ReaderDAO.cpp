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

Reader *ReaderDAO::searchByName(const string &name) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getUsername() == name) return all[i];
	return 0;
}

vector <Reader *> ReaderDAO::getAll() { return all; }
bool ReaderDAO::insert(Reader *p) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getUsername() == p->getUsername()) return false;
	all.push_back(p);
	return true;
}
bool ReaderDAO::erase(const string &username) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getUsername() == username) {
			all.erase(all.begin() + i);
			return true;
		}
	return false;
}
