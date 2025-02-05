#include "or.h"

int main()
{
	Date d1;
	//d1.printfData();
	Date d2(d1);
	//d2.printfData();
	Date d3 = d2 =d1;
	//d3.printfData();


	d1 += 100;
	d1.printfData();
	d3 = d2 + 100;
	d2.printfData();
	d3.printfData();

	Date d4(2024,10,10);
	//d1.printfData();
	Date d5(2025, 10, 10);
	cout << (d4 > d5) << endl;
	//d2.printfData();
	Date d6(2024, 11, 10);
	cout << (d4 > d6) << endl;
	Date d7(2024, 10, 11);
	cout << (d4 > d7) << endl;

	Date d8(2000, 12, 9);
	cout << (d4 > d8) << endl;
	cout << (d4 - d6) << endl;
	return 0;
}