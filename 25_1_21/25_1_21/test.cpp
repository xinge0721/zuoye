#include <stdio.h>
#include <stdlib.h>
#include <iostream>

#include "add.h"
namespace MyNamespace {
	void foo() {
		std::cout << "这是foo函数" << std::endl;
	}
	void bar() {
		std::cout << "这是bar函数" << std::endl;
	}
}
//using MyNamespace::foo;

void add(int x = 10, int y = 20)
{
	std::cout <<"这是整数"<< x + y << std::endl;
}
int add(int x = 10, int y = 20)
{
	std::cout << "这是浮点数" << x + y << std::endl;
	return 0;

}

int main()
{
	add(10, 10);
	add(10, 10);
	return 0;
}
