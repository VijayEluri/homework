#include "Kind.h"

Kind::Kind(const string &isbn, const wstring &name, const wstring &authors, const wstring &index)
	: isbn(isbn), name(name), authors(authors), index(index) {}

string Kind::getISBN() const { return isbn; }
wstring Kind::getName() const { return name; }
wstring Kind::getIndex() const { return index; }
vector <wstring> Kind::getAuthors() const { return split(authors); }
size_t Kind::countBooks() const { return all.size(); }
vector <Book *> Kind::getBooks() const { return all; }

void Kind::setISBN(const string &newisbn) {
	KindDAO t;
	t.erase(*this);
	isbn = newisbn;
	t.insert(*this);
}

void Kind::setIndex(const wstring &newindex) {
	KindDAO t;
	t.erase(*this);
	index = newindex;
	t.insert(*this);
}

void Kind::setAuthors(const wstring &newauthors) {
	KindDAO t;
	t.erase(*this);
	authors = newauthors;
	t.insert(*this);
}

void Kind::setName(const wstring &newname) {
	KindDAO t;
	t.erase(*this);
	name = newname;
	t.insert(*this);
}

