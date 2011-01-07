#include "Library.h"
#include "Kind.h"
#include "User.h"
#include "Reader.h"
#include "Admin.h"
#include "KindDAO.h"
#include "ReaderDAO.h"
#include "AdminDAO.h"
#include "BookDAO.h"
#include <algorithm>
using namespace std;

Library::~Library() {}

bool Library::initialize() {
	if (!KindDAO::loadAll()) return false;
	if (!ReaderDAO::loadAll()) return false;
	if (!AdminDAO::loadAll()) return false;
	if (!BookDAO::loadAll()) return false;
	return true;
}

void Library::finalize() {
	KindDAO::saveAll();
	ReaderDAO::saveAll();
	AdminDAO::saveAll();
	BookDAO::saveAll();
}

Reader *Library::readerLogin(const string &username, const string &password) {
	Reader *reader = ReaderDAO::searchByName(username);
	if (reader && reader->getPassword() == password) return reader;
	return 0;
}

Admin *Library::adminLogin(const string &username, const string &password) {
	Admin *admin = AdminDAO::searchByName(username);
	if (admin && admin->getPassword() == password) return admin;
	return 0;
}

vector <Kind *> Library::searchByISBN(const string &isbn) {
	Kind *kind = KindDAO::searchByISBN(isbn);
	vector <Kind *> ret;
	if (kind) ret.push_back(kind);
	return ret;
}

vector <Kind *> Library::searchByName(const wstring &name) {
	return KindDAO::searchByName(name);
}

vector <Kind *> Library::searchByAuthor(const wstring &author) {
	return KindDAO::searchByAuthor(author);
}

vector <Kind *> Library::searchByIndex(const wstring &index) {
	return KindDAO::searchByIndex(index);
}

vector <Kind *> Library::searchByExpression(const wstring &expr) {
	vector <Kind *> ret;
	// TO DO
	return ret;
}

vector <Kind *> Library::searchLikeName(const wstring &name) {
	vector <Kind *> ret;
	//TO DO
	return ret;
}

bool cmp_isbn(Kind *a, Kind *b) {
	return a->getISBN() < b->getISBN();
}
bool cmp_name(Kind *a, Kind *b) {
	return a->getName() < b->getName();
}
bool cmp_index(Kind *a, Kind *b) {
	return a->getIndex() < b->getIndex();
}
bool cmp_counts(Kind *a, Kind *b) {
	return a->countBooks() < b->countBooks();
}

void Library::reorderResults(vector <Kind *> &kinds, ReorderType type, bool desc) {
	if (type == ORDER_BY_ISBN) sort(kinds.begin(), kinds.end(), cmp_isbn);
	else if (type == ORDER_BY_NAME) sort(kinds.begin(), kinds.end(), cmp_name);
	else if (type == ORDER_BY_ISBN) sort(kinds.begin(), kinds.end(), cmp_index);
	else if (type == ORDER_BY_COPY_COUNT) sort(kinds.begin(), kinds.end(), cmp_counts);
	if (desc) reverse(kinds.begin(), kinds.end());
}
