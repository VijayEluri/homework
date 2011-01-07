#include "Book.h"
#include "KindDAO.h"
#include "BookDAO.h"
#include "Kind.h"
#include "DataToken.h"

Kind::~Kind() {}

Kind::Kind(const string &isbn, const wstring &name, const wstring &authors, const wstring &index)
	: isbn(isbn), name(name), authors(authors), index(index) {}

string Kind::getISBN() const { return isbn; }
wstring Kind::getName() const { return name; }
wstring Kind::getIndex() const { return index; }
vector <wstring> Kind::getAuthors() const { return split(authors); }
wstring Kind::getAuthorstr() const { return authors; }

size_t Kind::countBooks() const {
	vector <Book *> all = BookDAO::getAll();
	int count = 0;
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getKind().getISBN() == isbn)
			++ count;
	return count;
}
vector <Book *> Kind::getBooks() const {
	vector <Book *> all = BookDAO::getAll(), ret;
	for (int i = 0; i < (int)all.size(); ++ i)
		if (all[i]->getKind().getISBN() == isbn)
			ret.push_back(all[i]);
	return ret;
}

void Kind::setISBN(const string &newisbn) {
	KindDAO::erase(getISBN());
	isbn = newisbn;
	KindDAO::insert(this);
}

void Kind::setIndex(const wstring &newindex) {
	KindDAO::erase(getISBN());
	index = newindex;
	KindDAO::insert(this);
}

void Kind::setAuthors(const wstring &newauthors) {
	KindDAO::erase(getISBN());
	authors = newauthors;
	KindDAO::insert(this);
}

void Kind::setName(const wstring &newname) {
	KindDAO::erase(getISBN());
	name = newname;
	KindDAO::insert(this);
}

