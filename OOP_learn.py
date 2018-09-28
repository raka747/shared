# -*- coding: utf-8 -*-
"""
Spyder Editor

This is a temporary script file.
"""

class Test:
    def fun(self):
        print("Hello")
        

obj = Test()
obj.fun()

'''
The __init__ method, is similar to constructors in C++. It is run as soon as
an object of a class is instantiated.

Instance Variable: variables deifined within a constructor or method with self.
Can call class variable direclty using Class name.
'''
class Person:
    stream = "male" # class variable
    def __init__(self, name):
        self.name = name # instance variable
        
    def sethobby(self, hobby):
        print('Hello, my name is', self.name)
        self.hobby = hobby # instance variable
        
    def gethobby(self):
        return self.hobby
        
p = Person("Rajesh")
p.sethobby("cycling")

print(Person.stream)
print(p.stream)
print(p.gethobby())

'''
creating an empty class using "pass" statement
'''
class empty:
    pass


'''
Data hiding: __variablename , these type of varibles not visible outside class.
Private methods are accessible outside their class, just not easily accessible.
Nothing in Python is truly private; internally

-Can access hidden variables outside class with a tricky syntax.
'''

'''
__repr__ and __str__ methods used for printing in Python

if both __repr__ and __str__ methods in class then __str__ given preference
'''
class Test:
    
    # Hidden member of MyClass
    __hiddenVariable = 0
    
    # A member method that changes __hiddenVariable
    
    def add(self, increment):
        self.__hiddenVariable += increment
        print(self.__hiddenVariable)
    
    def __init__(self,a,b):
        self.a = a
        self.b = b
        
    def __repr__(self):
        return "Test a:%s b: %s" %(self.a, self.b)
    
t = Test(112,358)
#w/o __repr__, print(t) just results in location of Test instance.
print(t)

'''
if none of __repr__ and __str__, default printed.

default output example: <__main__.Test instance at 0x7fa079da6710>
'''

'''
Iterators in Python:
    Iterators in python is any type that can be used with a 'for in' loop. Examples
    of inbuilt iterators : lists, tuples, dicts and sets.
    
__iter__ method that is called on initialization of an iterator. This should return 
an object that has a next or __next__ (in Python 3) method.

next ( __next__ in Python 3) The iterator next method should return the next value 
for the iterable. 
When an iterator is used with a ‘for in’ loop, the for loop implicitly 
calls next() on the iterator object. This method should raise a StopIteration to signal 
the end of the iteration.
'''

# A user defined iterator
class Test:
    
    # constructor
    def __init__(self, limit):
        self.limit = limit
    
    # called when iteration is initialized
    def __iter__(self):
        self.x = 10
        return self
    
    def __next__(self):
        x  =self.x # store current value of x
        
        # Stop iteration if limit is reached
        if x > self.limit:
            raise StopIteration
 
        # Else increment and return old value
        self.x = x + 1;
        return x
        
for i in Test(15):
    print(i)
    

'''
Inheritance

Class (Base / Super class) inherited by another class (Sub Class).

Calling a super class function is possible if the same class is not present in sub class

issubclass(), isinstance() 

Python supports Multiple Inheritances like C++ and unlike Java
'''

class Person(object):
    
    def __init__(self, name):
        self.name  = name
        
    def getName(self):
        return self.name
    
    def isEmployee(self):
        return False
    
class Employee(Person):
    
    def isEmployee(self):
        return True
    
    
# Driver code
emp = Person("Raj") # An object of Person
print(emp.getName(), emp.isEmployee())

emp = Employee("Rajesh") # An object of Employee
print(emp.getName(), emp.isEmployee())



# Multiple Inheritance example

class Base1(object):
    
    def __init__(self):
        self.str1 = "ra1"
        print ("Base1")
        
class Base2(object):
    
    def __init__(self):
        self.str2 = "ra2"
        print("Base2")
        
class Derived(Base1, Base2):
    
    def __init__(self):
        #calling constructors of Bas1 and 2 classes
        Base1.__init__(self)
        Base2.__init__(self)
        print ("Delivered")
    def printStrs(self):
        print(self.str1, self.str2)

ob = Derived()
# attributes str1 and str2 will be added to "ob" object.
ob.printStrs()


'''
How to access parent members in a subclass
1. using super()
2. using parent class name

How are these 2 methods different? (next topic)
'''

# Using Parent class name:
# Python example to show that base class members can be accessed in
# derived class using base class name
class Base(object):
 
    # Constructor
    def __init__(self, x):
        self.x = x    
 
class Derived(Base):
 
    # Constructor
    def __init__(self, x, y):
        Base.x = x 
        self.y = y
 
    def printXY(self):
      
       # print(self.x, self.y) will also work
       print(Base.x, self.y)
 
 
# Driver Code
d = Derived(10, 20)
d.printXY()

# Using super(): We can also access parent class members using super.
# Python example to show that base class members can be accessed in
# derived class using super()

class Base(object):
 
    # Constructor
    def __init__(self, x):
        self.x = x    
 
class Derived(Base):
 
    # Constructor
    def __init__(self, x, y):
         
        ''' In Python 3.x, "super().__init__(name)"
            also works'''
        super(Derived, self).__init__(x)
        self.y = y
 
    def printXY(self):
 
       # Note that Base.x won't work here
       # because super() is used in constructor
       print(self.x, self.y)
 
 
# Driver Code
d = Derived(10, 20)
d.printXY()

###############################################################################

'''
Class or Static and Instance variables

class or static: shared by all objects
Instance or non-static: different for different objects

python doesn't use a "static" keyboard unlike Java and C++

'''

class CSStudent:
    stream = 'cse'      # class variable
    def __init__(self, name, roll):
        self.name = name  #instance variable
        self.roll = roll  #instance variable

a = CSStudent("rj", 1)
b = CSStudent("rajesh", 2)

print(a.stream)  # prints "cse"
print(b.stream)  # prints "cse"
print(a.name)    # prints "Geek"
print(b.name)    # prints "Nerd"
print(a.roll)    # prints "1"
print(b.roll)    # prints "2"

# Class variables can be accessed using class

print(CSStudent.stream) # prints "cse"

###############################################################################

'''
Changing Class Members in Python
# can't change class variable using object, a new instance variable for that 
particular object is created.

# correct way to change the value of class variable.
'''
CSStudent.stream = "ece"

print(CSStudent.stream)


'''
First Class functions:
 
Properties of first class functions:

A function is an instance of the Object type.
You can store the function in a variable.
You can pass the function as a parameter to another function.
You can return the function from a function.
You can store them in data structures such as hash tables, lists, …

http://www.geeksforgeeks.org/first-class-functions-python/
'''

'''
Exception handling
> try - except
> try - except - else
http://www.geeksforgeeks.org/python-set-5-exception-handling/
'''

try:
    a = 3
    if a < 4:
        #throws ZeroDivisionError for a = 3
        b = a/(a-3)
        c = a + 3
    # throws NameError if a >= 4; i.e. b won't be assigned if a >= 4
    print ("value of b = ", b)
    
# note braces () for multiple exceptions
except (ZeroDivisionError, NameError):
    print ("\nError occured and handled")
    
else:  # if no exception, it can use variables from try block
    print (b)
    print(c)
    print(b+c)
    
###############################################################################    
a