drop procedure if exists spGetTopScorer;

delimiter //
create procedure spGetTopScorer()
begin 
declare sql_error tinyint default false;
declare continue handler for sqlexception
set sql_error = true;

start transaction;
	SELECT firstName as FirstName, lastName as LastName, goals AS Goals FROM players WHERE goals IN (SELECT max(goals) FROM players);
    if sql_error = false then
		commit;
	else
		rollback;
	end if;

end//

call spGetTopScorer()
