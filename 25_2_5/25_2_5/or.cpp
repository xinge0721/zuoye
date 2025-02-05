#include "or.h"

void Date::printfData()
{
	cout << _year << "�� " << _month << "�� " << _day << "�� " << endl;
}
// ȫȱʡ�Ĺ��캯��
Date::Date(int year, int month , int day)
{
	_year = year;
	_month = month;
	_day = day;
}

// �������캯��

// d2(d1)

Date::Date(const Date& d)
{
	_year = d._year;
	_month = d._month;
	_day = d._day;
}



// ��ֵ���������

// d2 = d3 -> d2.operator=(&d2, d3)

Date& Date::operator=(const Date& d)
{
	_year = d._year;
	_month = d._month;
	_day = d._day;
	return *this;
}



// ��������

Date::~Date()
{
	_year = _month = _day = 0;
}



// ����+=����

Date& Date::operator+=(int day)
{
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



// ����+����

Date Date::operator+(int day)
{
	Date data = *this;
	data += day;
	return data;
}



// ����-����

Date Date::operator-(int day)
{
	Date data = *this;
	data -= day;
	return data;
}



// ����-=����

Date& Date::operator-=(int day)
{

	this->_day = this->_day - day;
	while (this->_day <= 0)
	{
		this->_day += GetMonthDay(this->_year, this->_month);
		this->_month--;
		if (this->_month == 0)
		{
			this->_year--;
			this->_month = 12;
		}
	}
	return *this;
}



// ǰ��++

Date& Date::operator++()
{
	*this += 1;
	return *this;
}



// ����++

Date Date::operator++(int)
{
	Date data = *this;
	*this += 1;
	return data;
}



// ����--

Date Date::operator--(int)
{
	*this -= 1;
	return *this;

}



// ǰ��--

Date& Date::operator--()
{
	Date data = *this;
	*this -= 1;
	return data;

}



// >���������

bool Date::operator>(const Date& d)
{
	if (this->_year > d._year)
		return 1;
	else if (this->_year == d._year &&
			this->_month > d._month)
		return 1;
	else if (this->_year == d._year &&
			this->_month == d._month&&
			this->_day > d._day)
		return 1;
	return 0;
}



// ==���������

bool Date::operator==(const Date& d) {
	return this->_day == d._day && this ->_month == d._month && this->_year == d._year;

}



// >=���������

bool Date::operator >= (const Date& d) {
	return *this > d || *this == d;
}


// <���������

bool Date::operator < (const Date& d) {
	return !(*this >= d);
}



// <=���������

bool Date::operator <= (const Date& d) {
	return !(*this > d);

}


// !=���������

bool Date::operator != (const Date& d) {
	return !(*this == d);

}


// ����-���� ��������

int Date::operator-(const Date& d) {
	Date max = *this;
	Date min = d;

	int falg = 1;

	if (*this < d)
	{
		max = d;
		min = *this;
		falg = -1;
	}
	int cont = 0;
	while (min != max)
	{
		++min;
		++cont;
	}

	return cont * falg;
}
