# CSC540 - Customer Loyalty Program
## CSC540 Project Team 28
* AISHWARYA RAMACHANDRAN (aramach6)
* SHREE LAKSHMI RAMASUBRAMANIAN (slramasu)
* ADITI BHAGAWAT (aabhagwa)
* AKSHAYA RAGHUTHAMAN (araghut)

## Tech stack:
* Java version 1.8
* Oracle SQL

## Instructions to run the application
* Make sure that you have Java version 1.8 and Oracle SQL installed on your system.
* The .jar file of the application can be found in _out/artifacts/CSC540_CustomerLoyaltyProgram_jar_ folder.
* Before running the application, create all the tables in the database and run the stored procedures. This can be done by running the DDL.sql file present in _LoyaltyProgram/SQL_ folder. 
  To the run the file in sqlplus simply execute the following command:
  * If the file is in the current working directory:
    ```
    SQL> @DDL.sql
    ```
  * If the file is in some other directory:
    ```
    SQL> @{path}DDL.sql
    ``` 
* Once the database setup is done, open a new terminal and execute the following command:
  ```
  java -jar CSC540-CustomerLoyaltyProgram.jar
  ```
 The application should now be up and running! 