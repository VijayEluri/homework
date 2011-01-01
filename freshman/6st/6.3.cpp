/*****************
 * Author: MRain
 * Homework 6.3
*****************/
#include <cstdio>
#include <cstring>
#include <iostream>
#include <algorithm>
using namespace std;

class reader {
	int no;
	char name[10];
	char dept[20];
public:
	reader(int n, char *nm, char *d) {
		no = n;
		strcpy(name, nm);
		strcpy(dept, d);
	}
};

struct BookRecord {
	int bookNo;
	BookRecord *next;
	BookRecord(int bookNo, BookRecord *next) : bookNo(bookNo), next(next) {}
};

class readerTeacher : public reader {
	enum {MAX = 10};
	int borrowed;
	BookRecord *record;
public:
	readerTeacher(int n, char *nm, char *d) : reader(n, nm, d) { borrowed = 0; record = 0; }
	bool bookBorrow(int bookNo);
	bool bookReturn(int bookNo);
	void show();
};

bool readerTeacher::bookBorrow(int bookNo) {
	if (borrowed >= MAX) return false;
	++ borrowed;
	if (!record) record = new BookRecord(bookNo, 0);
	else {
		BookRecord *p = record;
		while (p->next) p = p->next;
		p->next = new BookRecord(bookNo, 0);
	}
	return true;
}

bool readerTeacher::bookReturn(int bookNo) {
	BookRecord *p = record;
	if (record->bookNo == bookNo) {
		record = p->next;
		delete p;
	} else {
		while (p->next && p->next->bookNo != bookNo) p = p->next;
		if (!p->next) return false;
		BookRecord *tmp = p->next;
		p->next = tmp->next;
		delete(tmp);
	}
	return true;
}

void readerTeacher::show() {
	BookRecord *p = record;
	while (p) {
		cout << p->bookNo << endl;
		p = p->next;
	}
}
