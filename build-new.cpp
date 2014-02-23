#include "flann/headers/flann.h"
#include <iomanip>
#include <cstdlib>
#include <vector>
#include <iostream>
#include <sstream>
#include <string>
#include <fstream>
#include <ctime>
#include <iterator>

using namespace std;

float *centers = new float[149995*128];
FLANN_INDEX index;
int main(int argc,char* argv[]) {clock_t t1=clock();
	ifstream fcenter("esp.centers");
	string line;
	for (unsigned i=0;i<149995;i++) {
		getline(fcenter,line);
		istringstream iss(line);
		float a;
		for (unsigned j=0;j<128;j++) {
			iss>>a;
			centers[i*128+j]=a;
		}
	}
	float *speed;
	fcenter.close();	
	IndexParameters indexPara;
	indexPara.algorithm = KDTREE;
	indexPara.checks = 2048;
	indexPara.trees=8;
	indexPara.target_precision = -1;
	indexPara.build_weight=0.01;
	indexPara.memory_weight=1;
	index=flann_build_index(centers,149995,128,speed,&indexPara,0);
	cout<<"ready"<<endl;
	while(1) {
		string content;
		cin>>content;
		stringstream sss;
		sss<<"siftWin32 <"<<content<<" >query.key";
		system((sss.str()).c_str());
		ifstream f("query.key");
		vector<float> d;
		getline(f,line);
		while(getline(f,line)) {
			getline(fcenter,line);
			istringstream iss(line);
			float a;
			vector<string> tokens{istream_iterator<string>{iss},istream_iterator<string>{}};
			if (tokens.size()==4) {
				continue;
			}
			for (unsigned i=0;i<tokens.size();i++) {
				istringstream(tokens[i])>>a;
				d.push_back(a);
			}
		}
		float* feature=new float[d.size()];
		for (unsigned i=0;i<d.size();i++) {
			feature[i]=d[i];
		}
		f.close();
		FLANNParameters findPara;
		findPara.log_level=LOG_NONE;
		findPara.log_destination=NULL;
		findPara.random_seed=CENTERS_RANDOM;
		int* result=new int[d.size()/128];
		int neighbors=flann_find_nearest_neighbors_index(index,feature,d.size()/128,result,1,1024,&findPara);
		cout<<"ready"<<endl;
		for (unsigned i=0;i<d.size()/128;i++) {
			cout<<result[i]<<" ";
		}
		cout<<endl;
	}
}
