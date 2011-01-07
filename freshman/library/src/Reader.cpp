#include "User.h"
#include "BookDAO.h"
#include "Reader.h"
#include "Book.h"

bool Reader::borrow(Book &x) {
	if (getPenalty()) return false;
	Book *t = BookDAO::searchByID(x.getID());
	if (borrowed.size() >= getBorrowLimit()) return false;
	if (t->isLentOut() || !t->isAvailable()) return false;
	Reader *p = t->getReserver();
	if (p && (p->getUsername() != username)) return false;
	borrowed.push_back(t);
	t->setReader(this);
	return true;
}

bool Reader::returnBook(Book &x) {
	Book *t = BookDAO::searchByID(x.getID());
	if (t->getReader()->getUsername() != username) return false;
	for (int i = 0; i < (int)borrowed.size(); ++ i)
		if (borrowed[i] == t) {
			borrowed.erase(borrowed.begin() + i);
			break;
		}
	t->setReader(0);
	return true;
}

bool Reader::renew(Book &x) {
	if (getPenalty()) return false;
	Book *t = BookDAO::searchByID(x.getID());
	if (t->getReader()->getUsername() != username) return false;
	t->setReader(this);
	return true;
}

bool Reader::reserve(Book &x) {
	if (getPenalty()) return false;
	Book *t = BookDAO::searchByID(x.getID());
	if (t->getReserver()) return false;
	t->setReserver(this);
	return true;
}

vector <Book *> Reader::getBorrowedBooks() { return borrowed; }
vector <Book *> Reader::getReservedBooks() { return reserved; }

int Reader::getPenalty() {
	int cnt = 0;
	Date now = System::getCurrentDate();
	int expired = System::getBorrowedBookExpiredDays();
	for (int i = 0; i < (int)borrowed.size(); ++ i) {
		int t = now - borrowed[i]->getBorrowedDate() + 1;
		if (t >= expired)
			cnt += expired - t;
	}
	return cnt;
}
