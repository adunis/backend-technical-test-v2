## TUI DX Backend Techincal Test v2 <br> by Aleksandar Petrovic

API built with Spring-Boot with these endpoints:
* **Create Order (POST /api/v1/order)**: Creates an order in the database and a client only if it hasn't been created yet. A client is identified by its first name, last name and telephone. For example clients with the same first and last names but diffent phone numbers are possible.
* **Update Order (PATCH /api/v1/order)**: Can modify an order only if a certain amount of minutes have not passed yet since its creation. This variable is defined in the application configuration. 
* **Search Orders (POST /api/v1/search/order)**: An admin can search orders which clients first name or last name contains a certain input string. Basic Authentication has been implemented for this endpoint and there is only one admin account (username: admin, password: password).  

**Authentication header for Search Order**s: *Basic YWRtaW46cGFzc3dvcmQ=*

Documentation for input and output DTOs can be found at: http://localhost:8080/v3/api-docs and http://localhost:8080/v3/api-docs/ui

Configurable variables in application.yaml:
* item-price (double): determines the single price of a food item. Used for calculating the total of an order. (default: 1.33)
* minuteTimeLimitForUpdatingOrder (integer): minutes since the creation of an order that is possible to modify it (default: 5)
* accepted-order-quantities (integers split by a comma): possible quantities of a food item that can be ordered (default: 5, 10, 15)
