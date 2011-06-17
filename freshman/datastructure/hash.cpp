#include<iostream>
#include<sstream>
#include<cstdlib>
#include<ctime>
#include<cmath>
#include <cassert>
#include "HashSet.h"
#include "HashMap.h"

using namespace std;

class hashint {
public:
	static int hashcode(int a) {
		return a;
	}
};

class hashstring {
public:
	static int hashcode(string a) {
		int ret = 0;
		for (int i = 0; i < (int)a.length(); ++ i) {
			ret = ret * 13 + a[i];
		}
		return ret;
	}
};

string itos(const int &x){
	stringstream ss;
	ss<<x;
	return ss.str();
}
int main()
{
	HashMap <int, double ,hashint> H;
	HashSet <string ,hashstring> H2;
	/*ArrayList<int *> testdata;
	for(int i=0;i<100;i++){
		int *tmp=new int(i+10);
		testdata.forceset(i,tmp);
	}
	for(int i=0;i<10;i++){
		cout<<testdata.data[i]<<endl;
	}
	testdata.ensureCapacity(512);
	for(int i=0;i<20;i++){
		cout<<testdata.data[i]<<endl;
		cout<<*testdata.fetch(i)<<endl;
	}*/
	try{
		H.put(3,0.5);
		H2.add("ORZ ZJJ");
		H2.clear();
		H.clear();
		H.put(3,0.5);
		H.put(30,0.5);
		H.put(3,0.5);
		H.clear();
		srand(time(NULL));
		for(int i=0;i<13;i++){
			H.put(i,i);
			H2.add(itos(i));
		}
		for(HashMap<int,double,hashint>::Iterator itr(H.iterator());itr.hasNext();){
			Entry<int,double> tmp(itr.next());
			cout<<tmp.key<<" "<<tmp.value<<endl;
		}
		for(HashSet<string,hashstring>::Iterator itr(H2.iterator());itr.hasNext();){
			cout<<itr.next()<<endl;
		}
		H.clear();
		H2.clear();
		for(int i=0;i<50000;i++){
			assert(H.size()==0);
			assert(H.isEmpty());
			for(int j=0;j<200;j++){
				int tmp=rand();
				H.put(j,tmp);
				assert(H.containsKey(j));
				assert(H.containsValue(tmp));
				tmp=rand();
				H2.add(itos(tmp));
				assert(H.size()==j+1);
				assert(H2.contains(itos(tmp)));
			}
			assert(H.size()==200);
			try{
				H.clear();
				H2.clear();
			} catch(ElementNotExist e){
				cout<<e.getMessage()<<endl;
			}
		}
		
		H.put(555,0.789);
		H2.add("Orz Zjj");
		//H2.add("¿?¿?¿?¿?¿?");
		cout<<H.size()<<endl;
		cout<<H2.size()<<endl;
		for (HashMap<int, double,hashint>::Iterator i(H.iterator()); i.hasNext(); )
		{
			Entry<int, double> tmp(i.next());
			cout <<tmp.key<<" "<<tmp.value<<endl;
		}
		for(HashSet<string,hashstring>::Iterator i(H2.iterator());i.hasNext();){
			cout<<(i.next())<<endl;
		}

	}catch (IndexOutOfBound e){
		cout<<e.getMessage()<<endl;
	}
	return 0;
}
