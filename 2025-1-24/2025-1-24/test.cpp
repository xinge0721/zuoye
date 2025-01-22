#include <stdio.h>
#include <stdlib.h>
// 1. 正常的命名空间定义
namespace bit{	int rand = 10;}//int rand = 40;int main(){
	int rand = 20;
	// 这⾥默认是访问的是全局的rand函数指针
	printf("%d\n", rand);
	// 这⾥指定bit命名空间中的rand
	printf("%d\n", bit::rand);	// 这⾥指定全局中的rand	//printf("%d\n", ::rand);	return 0;}