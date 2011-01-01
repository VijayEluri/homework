/*****************
 * Author: MRain
 * Homework 3.5
*****************/
int **magic(int n) {
	int *storage = new int[n * n];
	int **matrix = new int*[n];
	matrix[0] = storage;
	for (int i = 1; i < n; ++ i) matrix[i] = matrix[i - 1] + n;
	int row = 0, col = (n - 1) / 2;
	matrix[row][col] = 1;
	for (int i = 2; i <= n * n; ++ i) {
		int nextrow = (row - 1 + n) % n,
			nextcol = (col + 1) % n;
		if (!matrix[nextrow][nextcol]) row = nextrow, col = nextcol;
		else ++ row;
		matrix[row][col] = i;
	}
	return matrix;
}

