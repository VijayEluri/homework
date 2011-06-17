#include<iostream>
#include <assert.h>
#include<sstream>
#include<cstdlib>
#include<ctime>
#include<cmath>
#include "TreeMap.h"
//#include "SBTreeSet.h"

using namespace std;

int main(){
	TreeMap<int,double> H;
	//TreeSet<string> H2;
	try{
		H.put(3,0.5);
		//H2.add("ORZ ZJJ");
		//H2.clear();
		H.clear();
		H.put(3,0.5);
		H.put(30,0.5);
		H.put(3,0.5);
		H.clear();
		srand(time(NULL));
		for(int i=0;i<13;i++){
			H.put(i,i);
			//H2.add(itos(i));
		}
		for(TreeMap<int,double>::Iterator itr(H.iterator());itr.hasNext();){
			Entry<int,double> tmp(itr.next());
			cout<<tmp.key<<" "<<tmp.value<<endl;
		}
	//	for(HashSet<string,hashstring>::Iterator itr(//H2.iterator());itr.hasNext();){
	//		cout<<itr.next()<<endl;
	//	}
		H.clear();
		//H2.clear();
		for(int i=0;i<50000;i++){
			assert(H.size()==0);
			assert(H.isEmpty());
			for(int j=0;j<200;j++){
				H.put(j,rand());
				//H2.add(itos(rand()));
				assert(H.size()==j+1);
			}
			assert(H.size()==200);
			try{
				H.clear();
				//H2.clear();
			} catch(ElementNotExist e){
				cout<<e.getMessage()<<endl;
			}
		}
		
		H.put(555,0.789);
		//H2.add("Orz Zjj");
		////H2.add("毛主席万岁");
		cout<<H.size()<<endl;
	//	cout<<//H2.size()<<endl;
		for (TreeMap<int, double>::Iterator i(H.iterator()); i.hasNext(); )
		{
			Entry<int, double> tmp(i.next());
			cout <<tmp.key<<" "<<tmp.value<<endl;
		}
	}catch (IndexOutOfBound e){
		cout<<e.getMessage()<<endl;
	}
	return 0;
}
