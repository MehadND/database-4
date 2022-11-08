# JDBC Project Folder Structure

- ### Data Visualization Folder
    In this folder, you will find all the exported data from the application. For example, the exported csv files and the exported jpeg/charts files.
    
- ### Database Setup Scripts
    This folder contains all the database scripts.
    - FootballDatabase.sql
        - This script is the main script of the project as it contains sql code, which creates database and table and then inserts few values into the table.
    - FootballDatabase_SP.sql
        - This script contains code for the Stored Procedure that my project is using.

- ### Java Project
    This folder contains the JDBC (Java) project.
    
    

    #### Instructions on running the Java project
    1. Open XAMPP and start the MySQL server on Port number 3307.
    2. Run MYSQL WorkBench, start any connection with port number 3307.
    3. Open *FootballDatabase.sql* and *FootballDatabase_SP.sql* by navigating 
        ```
        File > Open SQL Script
        ```


        Run these files
    4. Open the Java project in any IDE of your choice, and run the *MainClass.java* file.
         
    <br/>
    
    > Watch the screencast to learn more about how to navigate in the application.

- ### Other
    This folder contains all the relevant files, e.g. JDBC Assessment Report, Plagiarism Form and a screencast.