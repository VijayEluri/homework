/*****************
 * Author: MRain
 * Homework 2.5
*****************/
#include <cstdio>

char board[3][3];

void print_board() {
	puts("Chessboard: ");
	for (int i = 0; i < 3; ++ i) {
		for (int j = 0; j < 3; ++ j) {
			putchar(board[i][j]);
			if (j != 2) putchar('\t');
		}
		putchar('\n');
	}
}

bool check_end() {
	// for each row
	bool winflag;
	for (int i = 0; i < 3; ++ i) {
		winflag = true;
		for (int j = 1; j < 3; ++ j)
			if (board[i][j] == '?' || board[i][j] != board[i][j - 1]) {
				winflag = false;
				break;
			}
		if (winflag) {
			printf("Player %d wins!!\n", board[i][0] == 'O' ? 1 : 2);
			return true;
		}
	}
	// for each column
	for (int j = 0; j < 3; ++ j) {
		winflag = true;
		for (int i = 1; i < 3; ++ i)
			if (board[i][j] == '?' || board[i][j] != board[i - 1][j]) {
				winflag = false;
				break;
			}
		if (winflag) {
			printf("Player %d wins!!\n", board[0][j] == 'O' ? 1 : 2);
			return true;
		}
	}
	// diaonal
	winflag = true;
	for (int i = 1; i < 3; ++ i)
		if (board[i][i] == '?' || board[i][i] != board[i - 1][i - 1]) {
			winflag = false;
			break;
		}
	if (winflag) {
		printf("Player %d wins!!\n", board[0][0] == 'O' ? 1 : 2);
		return true;
	}
	// counter-diaonal
	winflag = true;
	for (int i = 1; i < 3; ++ i)
		if (board[i][2 - i] == '?' || board[i][2 - i] != board[i - 1][2 - i + 1]) {
			winflag = false;
			break;
		}
	if (winflag) {
		printf("Player %d wins!!\n", board[0][2] == 'O' ? 1 : 2);
		return true;
	}
	for (int i = 0; i < 3; ++ i)
		for (int j = 0; j < 3; ++ j)
			if (board[i][j] == '?') return false;
	puts("The game ends in a draw");
	return true;
}

int main() {
	for (int i = 0; i < 3; ++ i)
		for (int j = 0; j < 3; ++ j)
			board[i][j] = '?';
	print_board();
	int now = 1;
	while (!check_end()) {
		printf("Player %d's turn: ", now);
		int a, b;
		scanf("%d%d", &a, &b);
		board[a - 1][b - 1] = (now == 1 ? 'O' : 'X');
		putchar('\n');
		print_board();
		now = 3 - now;
	}
	return 0;
}
