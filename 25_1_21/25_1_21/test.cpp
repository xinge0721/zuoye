#include <stdio.h>
#include <stdlib.h>
#include <iostream>

#include "add.h"
namespace MyNamespace {
	void foo() {
		std::cout << "����foo����" << std::endl;
	}
	void bar() {
		std::cout << "����bar����" << std::endl;
	}
}
//using MyNamespace::foo;

void add(int x = 10, int y = 20)
{
	std::cout <<"��������"<< x + y << std::endl;
}
int add(int x = 10, int y = 20)
{
	std::cout << "���Ǹ�����" << x + y << std::endl;
	return 0;

}

int main()
{
	add(10, 10);
	add(10, 10);
	return 0;
}
