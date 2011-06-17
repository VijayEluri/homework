#include "User.h"
#include "ReaderDAO.h"
#include "Reader.h"
#include "Student.h"
#include "Teacher.h"
#include "System.h"
#include "Date.h"
#include <cstring>
#include <cstdlib>

vector <Reader *> ReaderDAO::all;

bool ReaderDAO::loadAll() {
	all.clear();
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getReaderFileName(), O_READ)) return false;
	void *token;
	int type;
	string username, password;
	while (token = file.read()) {
		type = *((int *)token);
		free(token);
		if (!(token = file.read())) return false;
		username = (char *)token;
		free(token);
		if (!(token = file.read())) return false;
		password = (char *)token;
		free(token);
		if (type == Reader::STUDENT) {
			Student *reader = new Student(username, password);
			all.push_back((Reader *)reader);
		} else {
			Teacher *reader = new Teacher(username, password);
			all.push_back((Reader *)reader);
		}
	}
	file.close();
	return true;
}

bool ReaderDAO::saveAll() {
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getReaderFileName(), O_WRITE)) return false;
	for (int i = 0; i < (int)all.size(); ++ i) {
		int type = all[i]->getType();
		if (!file.write((void *)&type, sizeof(int))) return false;
		string name = all[i]->getUsername(), pwd = all[i]->getPassword();
		if (!file.write((void *)name.c_str(), name.length() * sizeof(char))) return false;
		if (!file.write((void *)pwd.c_str(), pwd.length() * sizeof(char))) return false;
	}
	file.close();
	return true;
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
