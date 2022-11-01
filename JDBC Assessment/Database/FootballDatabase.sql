/*1. Creating Database*/
DROP DATABASE IF EXISTS DB_Football;
CREATE DATABASE IF NOT EXISTS DB_Football;
USE DB_Football;

/*2. Create tables*/
DROP TABLE IF EXISTS players;

CREATE TABLE players (
	player_id INTEGER AUTO_INCREMENT NULL PRIMARY KEY,
	firstName VARCHAR(20) NOT NULL,
	lastName VARCHAR(20) NOT NULL,
	age INTEGER NOT NULL,
	gender VARCHAR(1) NOT NULL,
	dob DATE,	#Format: YYYY-MM-DDnationalityclub
	nationality VARCHAR(20) NOT NULL,
	club VARCHAR(20) NOT NULL,
	appearances INTEGER NOT NULL,
	goals INTEGER NOT NULL,
	assists INTEGER NOT NULL
    );
    
/*2. Inserting Data*/
SELECT 'INSERTING DATA INTO DATABASE' as 'INFO';

INSERT INTO players VALUES (null, 'Mehad', 'Nadeem', 20, 'M', '2002-01-01'
, 'Irish', 'Athlone', 1, 1, 1);

INSERT INTO players VALUES (null, 'Jane', 'Doe', 20, 'F', '2002-11-11'
, 'English', 'Liverpool', 10, 1, 4);

select * from players;

