/*****************
 * Author: MRain
 * Homework 2.1
*****************/
#include <cstdio>

int n;

int main() {
	scanf("%d", &n);
	for (int a = 0; a < n; ++ a) {
		for (int i = 0; i < n - a - 1; ++ i) putchar(' ');
		for (int i = 0; i < 2 * a + 1; ++ i) putchar('*');
		putchar('\n');
	}
	return 0;
}
