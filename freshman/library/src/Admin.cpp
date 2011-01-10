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
	if (type == Reader::TEACHER) {
		Teacher *p = new Teacher(username, password);
		if (!ReaderDAO::insert(p)) {
			delete p;
			return false;
		}
	} else {
		Student *p = new Student(username, password);
		if (!ReaderDAO::insert(p)) {
			delete p;
			return false;
		}
	}
	return true;
}

bool Admin::removeReader(const string &username) {
	Reader *p = ReaderDAO::searchByName(username);
	if (!p) return false;
	ReaderDAO::erase(username);
	delete p;
	return true;
}

bool Admin::createAdmin(const string &username, const string &password) {
	Admin *p = new Admin(username, password);;
	if (!AdminDAO::insert(p)) {
		delete p;
		return false;
	}
	return true;
}

bool Admin::removeAdmin(const string &username) {
	Admin *p = AdminDAO::searchByName(username);
	if (!p) return false;
	AdminDAO::erase(username);
	delete p;
	return true;
}

bool Admin::createKind(const string &isbn, const wstring &name, const wstring &authors, const wstring &index) {
	if (KindDAO::searchByISBN(isbn)) return false;
	Kind *p = new Kind(isbn, name, authors, index);
	KindDAO::insert(p);
	return true;
}

bool Admin::removeKind(const string &isbn) {
	Kind *p = KindDAO::searchByISBN(isbn);
	if (!p || p->countBooks() > 0) return false;
	KindDAO::erase(isbn);
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
