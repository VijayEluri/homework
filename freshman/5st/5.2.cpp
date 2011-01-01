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
};

