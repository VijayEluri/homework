#include <cstdio>
#include <cstdlib>
#include <algorithm>
#include <iostream>
using namespace std;

int main() {
    int res1 = 0, res2 = 0;
    for (int i = 1; i <= 8; ++ i) {
        int a = 0, b = 0;
        for (int j = 1; j <= i; ++ j)
            a += (9 - j + 1) * (i - j + 1);
        for (int j = 1; j <= 9 - i; ++ j)
            b += 9 - j + 1;
        res1 += 2 * a * b;
    }
    for (int i = 1; i <= 8; ++ i) {
        for (int j = 1; j <= 8; ++ j) {
            int a = 0;
            for (int k = 1; k <= min(i, j); ++ k)
                a += (i - k + 1) * (j - k + 1);
            a *= min(9 - i, 9 - j);
            res2 += 2 * a;
        }
    }
    cout << res1 << ' ' << res2 << ' ' << res1 - res2 << endl;
    return 0;
}
