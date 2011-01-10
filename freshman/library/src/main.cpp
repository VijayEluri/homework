// This is ***just an example***.
// Author: Xiao Jia
// Date: 2010/12/02

#include "ILibrary.h"
#include "Library.h"
#include "Reader.h"
#include "Admin.h"
#include "Kind.h"
#include "KindDAO.h"
#include <iostream>
using namespace std;

int main() {
	ILibrary *lib = new Library();
	lib->initialize();
	
/*	Reader *reader = lib->readerLogin("5100309000", "secret");
	if (reader) {
		// do something with reader
	}*/
	string name, pwd;
	cout << "请输入用户名: ";
	cin >> name;
	cout << "请输入密码: ";
	cin >> pwd;
	Admin *admin = lib->adminLogin(name, pwd);
	if (!admin) {
		cout << "用户名或者密码错误" << endl;
		return 0;
	}
	cout << admin->getUsername() << "你好!" << endl;
	admin->createKind("0-1234567", L"这是一本书", L"不知道谁写的", L"没有索引");

	vector <Kind *> all = KindDAO::getAll();
	for (int i = 0; i < (int)all.size(); ++ i) {
		cout << "ISBN\t:\t" << all[i]->getISBN() << endl;
	}

	lib->finalize();
	delete lib;
	
	return 0;
}
