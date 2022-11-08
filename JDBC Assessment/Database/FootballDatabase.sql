/*1. Creating Database*/
DROP DATABASE IF EXISTS DB_Football;
CREATE DATABASE IF NOT EXISTS DB_Football;
USE DB_Football;

/*2. Create table*/
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
    
/*3. Inserting Data*/
SELECT 'INSERTING DATA INTO DATABASE' as 'INFO';

INSERT INTO players VALUES (null, 'Mohamed', 'Salah', 30, 'M', '1992-07-15'
, 'Egypt', 'Liverpool', 205, 124, 50);

INSERT INTO players VALUES (null, 'Son', 'Heung-Min', 30, 'M', '1992-07-08'
, 'South Korea', 'Tottenham Hotspur', 245, 96, 48);

INSERT INTO players VALUES (null, 'Harry', 'Kane', 29, 'M', '1993-07-28'
, 'England', 'Tottenham Hotspur', 295, 193, 44);

INSERT INTO players VALUES (null, 'Kevin', 'De Bruyne', 31, 'M', '1991-07-28'
, 'Belgium', 'Manchester City', 222, 60, 95);

INSERT INTO players VALUES (null, 'Diogo', 'Jota', 26, 'M', '1996-12-04'
, 'Portugal', 'Liverpool', 125, 40, 11);

INSERT INTO players VALUES (null, 'Jamie', 'Vardy', 35, 'M', '1987-01-11'
, 'England', 'Leicester City', 283, 134, 44);

INSERT INTO players VALUES (null, 'Bukayo', 'Saka', 21, 'M', '2001-09-05'
, 'England', 'Arsenal', 109, 21, 20);

INSERT INTO players VALUES (null, 'Emmanuel', 'Dennis', 24, 'M', '1997-11-15'
, 'Nigeria', 'Nottingham Forest', 41, 11, 6);

INSERT INTO players VALUES (null, 'Riyad', 'Mahrez', 31, 'M', '1991-03-21'
, 'Algeria', 'Manchester City', 263, 78, 51);

INSERT INTO players VALUES (null, 'Wilfried', 'Zaha', 29, 'M', '1992-11-10'
, 'Cote D’Ivoire', 'Crystal Palace', 289, 66, 28);

select * from players;



