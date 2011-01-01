/*****************
 * Author: MRain
 * Homework 3.3
*****************/
void makechange(int coins[], int differentCoins, int maxChange, int coinUsed[], int optimalStrategy[]) {
	// almost from books
	coinUsed[0] = 0;
	for (int cents = 1; cents <= maxChange; ++ cents) {
		int minCoins = cents, /*add*/strategy = 0;
		for (int j = 1; j < differentCoins; ++ j) {
			if (coins[j] > cents) continue;
			if (coinUsed[cents - coins[j]] + 1 < minCoins)
				minCoins = coinUsed[cents - coins[j]], /*add*/strategy = j;
		}
		coinUsed[cents] = minCoins;
		/*add*/optimalStrategy[cents] = strategy;
	}
}

void getStrategy(int coins[], int change, int optimalStrategy[], int result[]) {
	//Get a optimal Strategy of a certain amount of change
	//result stored in result[]
	int now = change;
	while (optimalStrategy[now]) {
		int strategy = optimalStrategy[now];
		++ result[strategy];
		now -= coins[strategy];
	}
}

