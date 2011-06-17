#include "AdminDAO.h"
#include "Admin.h"
#include "System.h"
#include "Date.h"
#include <cstdlib>

vector <Admin *> AdminDAO::all;

bool AdminDAO::loadAll() {
	all.clear();
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getAdminFileName(), O_READ)) return false;
	void *token;
	string username, password;
	while (token = file.read()) {
		username = (char *)token;
		free(token);
		if (!(token = file.read())) return false;
		password = (char *)token;
		free(token);
		Admin *admin = new Admin(username, password);
		all.push_back(admin);
	}
	file.close();
	return true;
}

bool AdminDAO::saveAll() {
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getAdminFileName(), O_WRITE)) return false;
	for (int i = 0; i < (int)all.size(); ++ i) {
		string name = all[i]->getUsername(), pwd = all[i]->getPassword();
		if (!file.write((void *)name.c_str(), name.length() * sizeof(char))) return false;
		if (!file.write((void *)pwd.c_str(), pwd.length() * sizeof(char))) return false;
	}
	file.close();
	return true;
}

Admin *AdminDAO::searchByName(const string &name) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getUsername() == name) return all[i];
	return 0;
}

vector <Admin *> AdminDAO::getAll() { return all; }
bool AdminDAO::insert(Admin *p) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getUsername() == p->getUsername()) return false;
	all.push_back(p);
	return true;
}
bool AdminDAO::erase(const string &username) {
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getUsername() == username) {
			all.erase(all.begin() + i);
			return true;
		}
	return false;
}
