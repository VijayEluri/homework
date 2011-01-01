/*****************
 * Author: MRain
 * Homework 3.4
*****************/
#include <cstdio>
#include <cstring>
#include <cctype>

int a, b;
char op;

bool expression_analysis(char *expression) {
	bool flag = false;
	int len = strlen(expression);
	a = b = 0;
	for (int i = 0; i < len; ++ i) {
		if (isdigit(expression[i])) {
			if (flag) b = b * 10 + expression[i] - '0';
			else a = a * 10 + expression[i] - '0';
		} else {
			if (flag) return false;
			flag = true;
			op = expression[i];
			if (op != '*' && op != '/' && op != '+' && op != '-') return false;
		}
	}
	return true;
}

int main(int argc, char *argv[]) {
	if (argc != 2) {
		puts("USAGE:\t./calc {expression}");
		puts("NOTE:\tMake sure that there is no white space in your expression.");
		return 1;
	}
	if (!expression_analysis(argv[1])) {
		puts("ERROR: Illegal expression.");
		return 2;
	}
	switch (op) {
		case '+':
			printf("%d\n", a + b);
			break;
		case '-':
			printf("%d\n", a - b);
			break;
		case '*':
			printf("%d\n", a * b);
			break;
		case '/':
			printf("%d\n", a / b);
	}
	return 0;
}
