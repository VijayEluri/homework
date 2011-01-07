#include "Admin.h"
#include "AdminDAO.h"
#include "Reader.h"
#include "Student.h"
#include "Teacher.h"
#include "ReaderDAO.h"
#include "Book.h"
#include "BookDAO.h"
#include "Kind.h"
#include "KindDAO.h"
#include <map>
using namespace std;

bool Admin::createReader(const string &username, const string &password, Reader::Type type) {
	ReaderDAO t;
	if (type == Reader::TEACHER) {
		Teacher *p = new Teacher(username, password);
		if (!t.insert(p)) {
			delete p;
			return false;
		}
	} else {
		Student *p = new Student(username, password);
		if (!t.insert(p)) {
			delete p;
			return false;
		}
	}
	return true;
}

bool Admin::removeReader(const string &username) {
	ReaderDAO t;
	Reader *p = t.searchByName(username);
	if (!p) return false;
	t.erase(username);
	delete p;
	return true;
}

bool Admin::createAdmin(const string &username, const string &password) {
	AdminDAO t;
	Admin *p = new Admin(username, password);;
	if (!t.insert(p)) {
		delete p;
		return false;
	}
	return true;
}

bool Admin::removeAdmin(const string &username) {
	AdminDAO t;
	Admin *p = t.searchByName(username);
	if (!p) return false;
	t.erase(username);
	delete p;
	return true;
}

bool Admin::createKind(const string &isbn, const wstring &name, const wstring &authors, const wstring &index) {
	KindDAO t;
	if (t.searchByISBN(isbn)) return false;
	Kind *p = new Kind(isbn, name, authors, index);
	t.insert(p);
}

bool Admin::removeKind(const string &isbn) {
	KindDAO t;
	Kind *p = t.searchByISBN(isbn);
	if (!p || p->countBooks() > 0) return false;
	t.erase(isbn);
	return true;
}

bool Admin::createBook(const Kind &k, bool borrowable) {
	KindDAO kinddao;
	BookDAO bookdao;
	Kind *kind = kinddao.searchByISBN(k.getISBN());
	Book *book = new Book(
				bookdao.getNextBookID(),
				kind,
				0,
				0,
				Date(0),
				Date(0),
				borrowable
			);
	if (bookdao.insert(book)) return true;
	delete(book);
	return false;
}

bool Admin::removeBook(const Book &t) {
	BookDAO bookdao;
	return bookdao.erase(t.getID());
}

vector <const Reader *> Admin::getAllReaders() {
	ReaderDAO readerdao;
	vector <Reader *> list = readerdao.getAll();
	vector <const Reader *> ret;
	for (int i = 0; i < (int)list.size(); ++ i)
		ret.push_back(list[i]);
	return ret;
}

vector <const Admin *> Admin::getAllAdmins() {
	AdminDAO admindao;
	vector <Admin *> list = admindao.getAll();
	vector <const Admin *> ret;
	for (int i = 0; i < (int)list.size(); ++ i)
		ret.push_back(list[i]);
	return ret;
}

Reader *Admin::getReaderByName(const string &name) {
	ReaderDAO readerdao;
	return readerdao.searchByName(name);
}

Admin *Admin::getAdminByName(const string &name) {
	AdminDAO admindao;
	return admindao.searchByName(name);
}
