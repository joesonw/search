#include "flann/headers/flann.h"
#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <algorithm>
#include <iterator>
#define CLUSTER_SIZE 150000
#define VECTOR_SIZE 128
using namespace std;
typedef vector<float> dary;
dary read(string);
void write(string,float*,int); 

int main(int argc, char* argv[]) {
	if (argc!=5) {
		cout<<"format:"<<endl;
		cout<<"build-trec [feature file] [size file] [image name list] [result]"<<endl;	
		exit(1);
	}
	cout<<"settings:"<<endl;
	cout<<"\tcluster size:"<<CLUSTER_SIZE<<endl;
	cout<<"\tvector size:"<<VECTOR_SIZE<<endl;
	string feature_file=argv[1];
	string size_file=argv[2];
	string img_list_file=argv[3];
	string trec_file=argv[4];
	// build cluster centers
	float *cluster_centers=new float[CLUSTER_SIZE*VECTOR_SIZE];
	dary feature_data=read(feature_file.c_str());
	int num_of_keys=feature_data.size()/VECTOR_SIZE;
	float *feature_dataset=new float[num_of_keys*VECTOR_SIZE];
	for (unsigned i=0;i<num_of_keys*VECTOR_SIZE;i++) {
		feature_dataset[i]=feature_data[i];
	}
	cout<<feature_file<<" loaded successfully"<<endl;
	IndexParameters clusterPara;
	clusterPara.algorithm=KMEANS;
	clusterPara.checks = 2048;
	clusterPara.cb_index=0.6;
	clusterPara.branching=10;
	clusterPara.iterations=15;
	clusterPara.centers_init=CENTERS_GONZALES;
	clusterPara.target_precision=-1;
	clusterPara.build_weight=0.01;
	clusterPara.memory_weight=1;
	cout<<"start calculating cluster centers"<<endl;
	int num_clusters = min(flann_compute_cluster_centers(feature_dataset,num_of_keys,VECTOR_SIZE,CLUSTER_SIZE,cluster_centers,&clusterPara,NULL),CLUSTER_SIZE);
	cout<<"calculation accomplished"<<endl;
	cout<<"num of clusters calculated: "<<num_clusters<<endl;
	float* tmp=new float[num_clusters*128];
	for (unsigned i=0;i<num_clusters*128;i++) {
		tmp[i]=cluster_centers[i];
	}
	delete cluster_centers;
	cluster_centers=tmp;
	IndexParameters indexPara;
	indexPara.algorithm = KDTREE;
	indexPara.checks = 2048;
	indexPara.trees=8;
	indexPara.target_precision = -1;
	indexPara.build_weight=0.01;
	indexPara.memory_weight=1;
	float* speed; //useless
	cout<<"start building indexes for cluster centers"<<endl;
	FLANN_INDEX index=flann_build_index(cluster_centers,num_clusters,128,speed,&indexPara,0);
	cout<<"index built successfully"<<endl;
	FLANNParameters findPara;
	findPara.log_level=LOG_NONE;
	findPara.log_destination=NULL;
	findPara.random_seed=CENTERS_RANDOM;
	int* neighbors_result=new int[num_of_keys];
	cout<<"start calculating nearest neighbors"<<endl;
	int neighbors=flann_find_nearest_neighbors_index(index,feature_dataset,num_of_keys,neighbors_result,1,1024,&findPara);
	cout<<"calculating nearest neighbors complete"<<endl;
	ifstream fo(size_file.c_str());
	ifstream ilist(img_list_file.c_str());
	ofstream result(trec_file.c_str());
	string line;
	vector<string> imgs;
	cout<<"start loading "<<img_list_file<<endl;
	while(getline(ilist,line)) {
		string n;
		istringstream iss(line);
		iss>>n;
		imgs.push_back(n);
	}
	ilist.close();
	cout<<img_list_file<<" loaded successfully"<<endl;
	int data_count=0;
	int count=0;
	cout<<"start writting result file to: "<<trec_file;
	cout<<" and loading "<<size_file<<" concurrently"<<endl;
	while(getline(fo,line)) {
		int s;
		istringstream iss(line);
		iss>>s;
		result<<"<DOC>"<<endl;
		result<<"<DOCNO>"<<imgs[count]<<"</DOCNO>"<<endl;
		result<<"<TEXT>"<<endl;
		for (unsigned i=0;i<s;i++) {
			result<<neighbors_result[i+data_count]<<" ";
		}
		result<<endl;
		result<<"</TEXT>"<<endl;
		result<<"</DOC>"<<endl;
		count++;
		data_count+=s;
	}
	result.close();
	fo.close();
	cout<<"finished"<<endl;
	cout<<"result file is stored in: "<<trec_file<<endl;
}
dary read(string name) {
	dary data;
	ifstream f(name.c_str());
	string line;
	int counter=0;
	while (getline(f,line)) {
		if (counter%10000 == 0)
			cout<<"reading "<<name<<" at line "<<counter<<endl;
		float a;
		istringstream iss(line);
		vector<string> tokens{istream_iterator<string>{iss},istream_iterator<string>{}};
		for (unsigned i=0;i<tokens.size();i++) {
			istringstream(tokens[i])>>a;
			data.push_back(a);
		}
		counter++;
	}
	f.close();
	return data;
}
void write(string name,float* dataset,int c) {
	ofstream f(name);
	for (unsigned i=0;i<c;i++) {
		if (i%10000==0)
			cout<<"writing "<<name<<" at line "<<i<<endl;
		for (unsigned j=0;j<127;j++) {
			f<<dataset[i*128+j]<<" ";
		}
		f<<dataset[i*128+127]<<endl;
	}	
}
