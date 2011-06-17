#include <cstdio>
#include <iostream>
#include <cstdlib>
#include <cstring>
#include <string>
#include <vector>
#include <algorithm>
using namespace std;

void error() {
	puts("Error");
	exit(0);
}

struct TData {
	bool syntax;
	int data;
	TData (bool syntax = false, int data = 0)
		: syntax(syntax), data(data) {}
};

char str[200];

void fetch() {
	int cnt = 0, n = strlen(str);
	for (int i = 0; i < n; ++ i) {
		if (str[i] == '(') ++ cnt;
		if (str[i] == ')') -- cnt;
		if (cnt < 0) error();
	}
	if (cnt) error();
	int *p = str;
}

int main() {
	freopen("input.txt", "r", stdin);
	gets(str);
	fetch();
	return 0;
}

