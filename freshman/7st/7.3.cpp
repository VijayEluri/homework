/*****************
 * Author: MRain
 * Homework 7.3
*****************/
#include <iostream>
#include <map>
#include <cstring>
using namespace std;

class SavingAccount {
private:
	double rate, savings;
	int account;
public:
	SavingAccount(double rate, double savings) 
		: rate(rate), savings(savings) {
		static int _account = 0;
		account = ++ _account;
	}
	int getAccount() { return account; }
	void modifyRate(double newrate) { rate = newrate; }
	double renew() { return savings = savings * (1 + rate); }
	double getSavings() { return savings; }
	void addSavings(double number) {
		savings += number;
	}
};

map <int, SavingAccount *> tree;

void CreateAccount(int savings, double rate = 0.01) {
	SavingAccount *p = new SavingAccount(savings, rate);
	tree[p->getAccount()] = p;
}

bool DeleteAccount(int account) {
	map <int, SavingAccount *>::iterator obj = tree.find(account);
	if (obj == tree.end()) return false;
	delete obj->second;
	tree.erase(obj);
	return true;
}

bool Save(int account, double number) {
	if (!tree.count(account)) return false;
	tree[account]->addSavings(number);
	return true;
}

bool Draw(int account, double number) {
	if (!tree.count(account)) return false;
	tree[account]->addSavings(-number);
	return true;
}

double Query(int account) {
	if (!tree.count(account)) throw "No account";
	return tree[account]->getSavings();
}

