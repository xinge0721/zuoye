#include <iostream>
using std::cin;
using std::cout;
using std::endl;
// ����-���� ��������
class Date{
public:
    // ��ȡĳ��ĳ�µ�����
    int GetMonthDay(int year, int month) {

        static int mymonth[] = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        if (((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) && month == 2)
            return mymonth[month] + 1;
        return  mymonth[month];
    }
    
    Date(int year, int month, int day)
    {
        _year = year;
        _month = month;
        _day = day;
    }

    // �������캯��

    // d2(d1)

    Date(const Date& d)
    {
        _year = d._year;
        _month = d._month;
        _day = d._day;
    }

    // ����+=����

    Date& operator+=(int day){
	this->_day += day;
	while (this->_day > GetMonthDay(this->_year, this->_month))
	{
		this->_day -= GetMonthDay(this->_year, this->_month);
		this->_month++;
		if (this->_month == 13)
		{
			this->_year++;
			this->_month = 1;
		}
	}
	return *this;
}


    // ����-=
    // ǰ��++

    Date& operator++()
    {
        *this += 1;
        return *this;
    }



    // ����++

    Date operator++(int)
    {
        Date data = *this;
        *this += 1;
        return data;
    }




    // ����--

    Date operator--(int);



    // ǰ��--

    Date& operator--();




    // >���������

    bool operator>(const Date& d) {
        if (this->_year > d._year)
            return 1;
        else if (this->_year == d._year &&
            this->_month > d._month)
            return 1;
        else if (this->_year == d._year &&
            this->_month == d._month &&
            this->_day > d._day)
            return 1;
        return 0;
    }



    // ==���������

    bool operator==(const Date& d) {
        return this->_day == d._day && this->_month == d._month &&
            this->_year == d._year;

    }



    // >=���������

    bool operator >= (const Date& d) {
        return *this > d || *this == d;
    }


    // <���������

    bool operator < (const Date& d) {
        return !(*this >= d);
    }



    // <=���������

    bool operator <= (const Date& d) {
        return !(*this > d);

    }


    // !=���������

    bool operator != (const Date& d) {
        return !(*this == d);

    }



    // ����-���� ��������

    int operator-(const Date& d) {
        Date max = *this;
        Date min = d;

        int falg = 1;

        if (*this < d) {
            max = d;
            min = *this;
            falg = -1;
        }
        int cont = 0;
        while (min != max) {
            ++min;
            ++cont;
        }

        return cont * falg;
    }


private:

    int _year;

    int _month;

    int _day;

};




int main() {
    int day1, day2, mon1, mon2, year1, year2;
    scanf("%4d%2d%2d", &year1, &mon1, &day1);
    scanf("%4d%2d%2d", &year2, &mon2, &day2);
    Date d1(2011, 04, 12);
    Date d2(2011, 04, 22);
    cout << d1 - d2 << endl;
}
// 64 λ������� printf("%lld")