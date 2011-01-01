/*****************
 * Author: MRain
 * Homework 2.3
*****************/
#include <iostream>
#include <algorithm>
#include <string>
using namespace std;

int main() {
	string t = "ABC";
	do {
		cout << t << endl;
	}while (next_permutation(t.begin(), t.end()));
	return 0;
}
