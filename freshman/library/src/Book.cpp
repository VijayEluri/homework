#include "Book.h"
#include "KindDAO.h"

Kind &Book::getKind() const { return *kind; }
void Book::setKind(const Kind &newkind) {
	KindDAO dao;
	kind = dao.searchByISBN(newkind.getISBN());
} 

Reader *Book::getReader() const { return reader; }
void Book::setReader(const Reader *newreader) { reader = newreader; }

Reader *Book::getReserver() const { return reserver; }
void Book::setReserver(const Reader *newreserver) { reserver = newreserver; }

Date Book::getBorrowedDate() const { return borrowed; }

bool Book::isLentOut() const { return (reader != 0); }
int Book::getID() const { return id; }

Book::Book() {
	BookDAO dao;
	id = dao.getNextBookID();
}
