# -*- coding: utf-8 -*-
"""
Created on Fri Aug 18 16:56:33 2017

@author: rpothams
"""

'''
Car base sale price is different from Truck

Motor Cycle number of wheels different from Car and Truck

base_sale_price and wheels are specific variables to the type of vehicle

Hence do not instantiate Vehicle class with these variables.

More info abt the below program

https://jeffknupp.com/blog/2014/06/18/improve-your-python-python-classes-and-object-oriented-programming/

'''

from abc import ABCMeta, abstractmethod

class Vehicle(object):
    '''
    Vehicle is ab abstract class. i.e it is inherited but shouldnt be used for creating
    an instance.

    To create an Abstract Base Class (ABC), class must have ABCMeta and an absract method
    '''

    __metaclass__ = ABCMeta

    base_sale_price = 0 # class variable
    wheels = 0

    def __init__(self, miles, make, model, year, sold_on):
        self.miles = miles # instance variable
        self.make = make
        self.model = model
        self.year = year
        self.sold_on = sold_on


    def sale_price(self):
        if self.sold_on is not None:
            return 0.0 # already sold
        return 5000.0 * self.wheels


    def purchase_price(self):
        if self.sold_on is None:
            return 0.0 # Not yet sold
        return self.base_sale_price - (.10 * self.miles)

    @abstractmethod
    def vehicle_type(self):
        pass


class Car(Vehicle):

    base_sale_price = 8000 #specific to Car class
    wheels = 4

    def vehicle_type(self):
        return 'car'

class Truck(Vehicle):

    base_sale_price = 10000
    wheels = 4

    def vehicle_type(self):
        return 'truck'

class Motorcycle(Vehicle):

    base_sale_price = 4000
    wheels = 2

    def vehicle_type(self):
        return 'motorcycle'

'''
Class Method & Static method

https://stackoverflow.com/questions/12179271/meaning-of-classmethod-and-staticmethod-for-beginner

https://stackoverflow.com/questions/136097/what-is-the-difference-between-staticmethod-and-classmethod-in-python
'''

class Date(object):

    def __init__(self, day = 0, month = 0, year = 0):
        self.day = day
        self.month = month
        self.year = year

    @classmethod
    def from_string(cls, date_as_string): # cls which is class not an instanceis a default parameter
        day, month, year = map(int, date_as_string.split('-'))
        date1 = cls(day, month, year)
        return date1

    @staticmethod
    def is_date_valid(date_as_string):
        day, month, year = map(int, date_as_string.split('-'))
        return day <= 31 and month <= 12 and year >= 1990

    def __repr__(self):
        return "Test a:%i b: %i" %(self.day, self.month)

date = Date()
datez = date.from_string("30-01-1992")

datex = Date.from_string("30-07-1992")

datey = Date.is_date_valid("30-07-1992")


'''
Class variable

In Python class variables are static variables
'''

# Program to show how to make changes to the class variable in Python

# Class for Computer Science Student
class CSStudent:
    stream = 'cse'     # Class Variable
    def __init__(self, name, roll):
        self.name = name
        self.roll = roll

# New object for further implementation
a = CSStudent("check", 3)
print ("a.stream =", a.stream)

# Correct way to change the value of class variable
CSStudent.stream = "mec"
print ("\nClass variable changes to mec")

# New object for further implementation
b = CSStudent("carter", 4)

print ("\nValue of variable stream for each object")
print ("a.stream =", a.stream)
print ("b.stream =", b.stream)

# This thing doesn't change class(static) variable
# Instead creates instance variable for the object
# 'a' that shadows class member.
a.stream = "ece"

print ("\nAfter changing a.stream")
print ("a.stream =", a.stream)
print ("b.stream =", b.stream)


'''
Functions can take another function as Input and can also return another function
'''

def create_adder(x):
    def adder(y):
        return x+y

    return adder

add_15 = create_adder(15)

print (add_15(10))


'''
@property or property = ()

https://www.programiz.com/python-programming/property

Interesting
'''