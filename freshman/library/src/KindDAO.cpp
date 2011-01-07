#include "KindDAO.h"
#include "DataToken.h"
#include "Kind.h"
#include <cstdlib>
#include <vector>
#include <algorithm>
using namespace std;

vector <Kind *> KindDAO::all;
map <wstring, set<Kind *> > KindDAO::mapByAuthor;
map <wstring, set<Kind *> > KindDAO::mapByIndex;
map <wstring, set<Kind *> > KindDAO::mapByName;
map <string, Kind *> KindDAO::mapByISBN;

bool KindDAO::loadAll() {
	all.clear();
	mapByISBN.clear();
	mapByName.clear();
	mapByAuthor.clear();
	mapByIndex.clear();
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getKindFileName(), O_READ)) return false;
	void *token = 0;
	string ISBN;
	wstring name, index, authors;
	while (token = file.read()) {
		ISBN = (char *)token;
		free(token);
		if (!(token = file.read())) return false;
		name = (wchar_t *)token;
		free(token);
		if (!(token = file.read())) return false;
		index = (wchar_t *)token;
		free(token);
		if (!(token = file.read())) return false;
		authors = (wchar_t *)token;
		free(token);
		Kind *kind = new Kind(ISBN, name, authors, index);
		all.push_back(kind);
	}
	file.close();
	for (int i = 0; i < (int)all.size(); ++ i) {
		Kind *kind = all[i];
		//mapByAuthor[kind->getAuthors()].insert(kind);
		mapByIndex[kind->getIndex()].insert(kind);
		mapByISBN[kind->getISBN()] = kind;
		mapByName[kind->getName()].insert(kind);
		vector <wstring> author = kind->getAuthors();
		for (int i = 0; i < (int)author.size(); ++ i)
			mapByAuthor[author[i]].insert(kind);
	}
	return true;
}

bool KindDAO::saveAll() {
	DataToken file;
	System sys;
	if (!file.open(sys.getWorkingDirectory() + sys.getKindFileName(), O_WRITE)) return false;
	for (int i = 0; i < (int)all.size(); ++ i) {
		bool flag = true;
		flag &= file.write(all[i]->getISBN().c_str(), all[i]->getISBN().length() * sizeof(char));
		flag &= file.write(all[i]->getName().c_str(), all[i]->getName().length() * sizeof(wchar_t));
		flag &= file.write(all[i]->getAuthorstr().c_str(), all[i]->getAuthorstr().length() * sizeof(wchar_t));
		flag &= file.write(all[i]->getIndex().c_str(), all[i]->getIndex().length() * sizeof(wchar_t));
		if (!flag) {
			file.close();
			return false;
		}
	}
	file.close();
	return true;
}

bool KindDAO::insert(Kind *kind) {
	if (mapByISBN.count(kind->getISBN())) return false;
	all.push_back(kind);
	mapByISBN[kind->getISBN()] = kind;
	mapByIndex[kind->getIndex()].insert(kind);
	mapByName[kind->getName()].insert(kind);
	vector <wstring> author = kind->getAuthors();
	for (int i = 0; i < (int)author.size(); ++ i)
		mapByAuthor[author[i]].insert(kind);
	return true;
}

bool KindDAO::erase(const string &isbn) {
	if (!mapByISBN.count(isbn)) return false;
	Kind *target = mapByISBN[isbn];
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getISBN() == isbn) {
			all.erase(all.begin() + i);
			break;
		}
	mapByIndex[target->getIndex()].erase(target);
	mapByName[target->getName()].erase(target);
	vector <wstring> author = target->getAuthors();
	for (int i = 0; i < (int)author.size(); ++ i)
		mapByAuthor[author[i]].erase(target);
	mapByISBN.erase(isbn);
	return true;
}

vector <Kind *> KindDAO::getAll() { return all; }

Kind *KindDAO::searchByISBN(const string &isbn) {
	return mapByISBN[isbn];
}

vector <Kind *>KindDAO::searchByAuthor(const wstring &author) {
	set<Kind *>::iterator it;
	set<Kind *> &list = mapByAuthor[author];
	vector <Kind *> ret;
	for (it = list.begin(); it != list.end(); ++ it)
		ret.push_back(*it);
	return ret;
}

vector <Kind *>KindDAO::searchByIndex(const wstring &index) {
	set<Kind *>::iterator it;
	set<Kind *> &list = mapByIndex[index];
	vector <Kind *> ret;
	for (it = list.begin(); it != list.end(); ++ it)
		ret.push_back(*it);
	return ret;
}

vector <Kind *>KindDAO::searchByName(const wstring &name) {
	vector <Kind *> ret;
	map <int, set<Kind *> > tree;
	map <int, set<Kind *> >::iterator it;
	set <Kind *>::iterator sit;
	tree.clear();
	for (int i = 0; i < (int)all.size(); ++ i) {
		int t = compare(all[i]->getName(), name);
		if (t != 0)
			tree[t].insert(all[i]);
	}
	for (it = tree.begin(); it != tree.end(); ++ it) {
		set <Kind *> &s = it->second;
		for (sit = s.begin(); sit != s.end(); ++ sit)
			ret.push_back(*sit);
	}
	reverse(ret.begin(), ret.end());
	return ret;
}
