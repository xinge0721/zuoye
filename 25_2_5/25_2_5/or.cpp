#include "or.h"

void Date::printfData()
{
	cout << _year << "年 " << _month << "月 " << _day << "日 " << endl;
}
// 全缺省的构造函数
Date::Date(int year, int month , int day)
{
	_year = year;
	_month = month;
	_day = day;
}

// 拷贝构造函数

// d2(d1)

Date::Date(const Date& d)
{
	_year = d._year;
	_month = d._month;
	_day = d._day;
}



// 赋值运算符重载

// d2 = d3 -> d2.operator=(&d2, d3)

Date& Date::operator=(const Date& d)
{
	_year = d._year;
	_month = d._month;
	_day = d._day;
	return *this;
}



// 析构函数

Date::~Date()
{
	_year = _month = _day = 0;
}



// 日期+=天数

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



// 日期+天数

Date Date::operator+(int day)
{
	Date data = *this;
	data += day;
	return data;
}



// 日期-天数

Date Date::operator-(int day)
{
	Date data = *this;
	data -= day;
	return data;
}



// 日期-=天数

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



// 前置++

Date& Date::operator++()
{
	*this += 1;
	return *this;
}



// 后置++

Date Date::operator++(int)
{
	Date data = *this;
	*this += 1;
	return data;
}



// 后置--

Date Date::operator--(int)
{
	*this -= 1;
	return *this;

}



// 前置--

Date& Date::operator--()
{
	Date data = *this;
	*this -= 1;
	return data;

}



// >运算符重载

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



// ==运算符重载

bool Date::operator==(const Date& d) {
	return this->_day == d._day && this ->_month == d._month && this->_year == d._year;

}



// >=运算符重载

bool Date::operator >= (const Date& d) {
	return *this > d || *this == d;
}


// <运算符重载

bool Date::operator < (const Date& d) {
	return !(*this >= d);
}



// <=运算符重载

bool Date::operator <= (const Date& d) {
	return !(*this > d);

}


// !=运算符重载

bool Date::operator != (const Date& d) {
	return !(*this == d);

}


// 日期-日期 返回天数

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
