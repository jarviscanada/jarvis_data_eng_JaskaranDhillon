# Introduction
This project was designed to help software developers learn the basics of RDBMS and working with SQL queries. 
A local PostgreSQL container was provisioned using Docker and Bash, where the sample database was constructed. 
The psql cli was used to connect to the database and insert sample data from the clubdata.sql file.
The queries below cover a wide variety of SQL operations, providing a comprehensive hands-on experience to help developer refine their SQL skills.

# SQL Queries

###### Table Setup (DDL)
```sql
  CREATE TABLE cd.members
    (
       memid integer NOT NULL, 
       surname character varying(200) NOT NULL, 
       firstname character varying(200) NOT NULL, 
       address character varying(300) NOT NULL, 
       zipcode integer NOT NULL, 
       telephone character varying(20) NOT NULL, 
       recommendedby integer,
       joindate timestamp NOT NULL,
       CONSTRAINT members_pk PRIMARY KEY (memid),
       CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby)
       REFERENCES cd.members(memid) ON DELETE SET NULL
    );
        
  CREATE TABLE cd.facilities
    (
       facid integer NOT NULL, 
       name character varying(100) NOT NULL, 
       membercost numeric NOT NULL, 
       guestcost numeric NOT NULL, 
       initialoutlay numeric NOT NULL, 
       monthlymaintenance numeric NOT NULL, 
       CONSTRAINT facilities_pk PRIMARY KEY (facid)
    );
  
  CREATE TABLE cd.bookings
    (
       bookid integer NOT NULL, 
       facid integer NOT NULL, 
       memid integer NOT NULL, 
       starttime timestamp NOT NULL,
       slots integer NOT NULL,
       CONSTRAINT bookings_pk PRIMARY KEY (bookid),
       CONSTRAINT fk_bookings_facid FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
       CONSTRAINT fk_bookings_memid FOREIGN KEY (memid) REFERENCES cd.members(memid)
    );
          
          
```

###### Question 0: Show all members

```sql
SELECT *
FROM cd.members
```

###### Question 1: Insert some data into a table

```sql 
INSERT INTO cd.facilities (
  facid, name, membercost, guestcost, 
  initialoutlay, monthlymaintenance
) 
VALUES 
  (9, 'Spa', 20, 30, 100000, 800);
```

###### Question 2: Insert calculated data into a table

```sql 
INSERT INTO cd.facilities (
  facid, name, membercost, guestcost, 
  initialoutlay, monthlymaintenance
) 
VALUES 
  (
    (
      SELECT 
        MAX(facid) 
      FROM 
        cd.facilities
    )+ 1, 
    'Spa', 
    20, 
    30, 
    100000, 
    800
  );
```

###### Question 3: Update some existing data

```sql 
UPDATE cd.facilities SET initialoutlay = 10000 WHERE facid = 1;
```

###### Question 4: Update a row based on the contents of another row

```sql 
UPDATE 
  cd.facilities 
SET 
  membercost = (
    SELECT 
      membercost 
    FROM 
      cd.facilities 
    WHERE 
      facid = 0
  ) * 1.1, 
  guestcost = (
    SELECT 
      guestcost 
    FROM 
      cd.facilities 
    WHERE 
      facid = 0
  ) * 1.1 
WHERE 
  facid = 1
```

###### Question 5: Delete all bookings

```sql 
DELETE FROM cd.bookings;
```

###### Question 6: Delete a member from the cd.members table

```sql 
DELETE FROM cd.members WHERE memid=37;
```

###### Question 7: Control which rows are retrieved

```sql 
SELECT 
  facid, 
  name, 
  membercost, 
  monthlymaintenance 
FROM 
  cd.facilities 
WHERE 
  membercost > 0 
  AND membercost < monthlymaintenance / 50;
```

###### Question 8: Basic string searches

```sql 
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  name LIKE '%Tennis%';
```

###### Question 9: Matching against multiple possible values

```sql 
SELECT 
  * 
FROM 
  cd.facilities 
WHERE 
  facid IN (1, 5);
```

###### Question 10: Working with dates

```sql 
SELECT 
  memid, 
  surname, 
  firstname, 
  joindate 
FROM 
  cd.members 
WHERE 
  joindate > '2012-09-01 0:0:0’;
```

###### Question 11: Combining results from multiple queries

```sql 
(
  SELECT 
    surname 
  FROM 
    cd.members
) 
UNION 
  (
    SELECT 
      name 
    FROM 
      cd.facilities
  );
```

###### Question 12: Retrieve the start times of members' bookings

```sql 
SELECT 
  starttime 
FROM 
  cd.members 
  JOIN cd.bookings ON cd.bookings.memid = cd.members.memid 
WHERE 
  firstname = 'David' 
  AND surname = 'Farrell’;
```

###### Question 13: Work out the start times of bookings for tennis courts

```sql 
SELECT 
  starttime, 
  name 
FROM 
  cd.bookings 
  JOIN cd.facilities ON cd.bookings.facid = cd.facilities.facid 
WHERE 
  starttime >= '2012-09-21' 
  AND starttime < '2012-09-22' 
  AND cd.facilities.name LIKE '%Tennis Court%' 
ORDER BY 
  starttime ASC;
```


###### Question 14: Produce a list of all members, along with their recommender

```sql 
SELECT 
  mems.firstname as memfname, 
  mems.surname as memsname, 
  recs.firstname as recfname, 
  recs.surname as recsname 
FROM 
  cd.members mems 
  LEFT JOIN cd.members recs ON recs.memid = mems.recommendedby 
ORDER BY 
  memsname, 
  memfname;
```

###### Question 15: Produce a list of all members who have recommended another member

```sql 
SELECT 
  DISTINCT recs.firstname as firstname, 
  recs.surname as surname 
FROM 
  cd.members mems 
  JOIN cd.members recs ON recs.memid = mems.recommendedby 
ORDER BY 
  surname, 
  firstname;
```

###### Question 16: Produce a list of all members, along with their recommender, using no joins.

```sql 
select 
  mems.firstname || ' ' || mems.surname as member, 
  (
    select 
      recs.firstname || ' ' || recs.surname as recommender 
    from 
      cd.members recs 
    where 
      recs.memid = mems.recommendedby
  ) 
from 
  cd.members mems 
order by 
  member;
```

###### Question 17: Count the number of recommendations each member makes.

```sql 
SELECT 
  recommendedby, 
  COUNT(*) 
FROM 
  cd.members 
WHERE 
  recommendedby IS NOT NULL 
GROUP BY 
  recommendedby 
ORDER BY 
  recommendedby;
```

###### Question 18: List the total slots booked per facility

```sql 
SELECT 
  facid, 
  SUM(slots) as "Total Slots" 
FROM 
  cd.bookings 
GROUP BY 
  facid 
ORDER BY 
  facid;
```

###### Question 19: List the total slots booked per facility in a given month

```sql 
SELECT 
  facid, 
  SUM(slots) as "Total Slots" 
FROM 
  cd.bookings 
WHERE 
  starttime >= '2012-09-01' 
  AND starttime < '2012-10-01' 
GROUP BY 
  facid 
ORDER BY 
  SUM(slots);
```

###### Question 20: List the total slots booked per facility per month

```sql 
SELECT 
  facid, 
  EXTRACT(
    MONTH 
    FROM 
      starttime
  ) AS month, 
  SUM (slots) AS "Total Slots" 
FROM 
  cd.bookings 
where 
  extract(
    year 
    from 
      starttime
  ) = 2012 
GROUP BY 
  facid, 
  month 
order by 
  facid, 
  month;
```

###### Question 21: Find the count of members who have made at least one booking

```sql 
SELECT 
  COUNT(DISTINCT memid) 
FROM 
  cd.bookings;
```

###### Question 22: List each member's first booking after September 1st 2012

```sql 
SELECT 
  surname, 
  firstname, 
  cd.members.memid, 
  min(starttime) 
FROM 
  cd.members 
  JOIN cd.bookings ON cd.bookings.memid = cd.members.memid 
WHERE 
  starttime >= '2012-09-01' 
GROUP BY 
  surname, 
  firstname, 
  cd.members.memid 
ORDER BY 
  cd.members.memid;
```

###### Question 23: Produce a list of member names, with each row containing the total member count

```sql 
select 
  count(*) over(), 
  firstname, 
  surname 
from 
  cd.members 
order by 
  joindate;
```

###### Question 24: Produce a numbered list of members

```sql 
select 
  ROW_NUMBER() over(), 
  firstname, 
  surname 
from 
  cd.members 
order by 
  joindate;
```

###### Question 25: Output the facility id that has the highest number of slots booked, again

```sql 
select 
  facid, 
  total 
from 
  (
    select 
      facid, 
      sum(slots) AS total, 
      rank() over (
        order by 
          sum(slots) desc
      ) rank 
    from 
      cd.bookings 
    group by 
      facid
  ) as ranked 
where 
  rank = 1
```

###### Question 26: Format the names of members

```sql 
SELECT 
  surname || ', ' || firstname AS name 
FROM 
  cd.members;
```

###### Question 27: Find telephone numbers with parentheses

```sql 
SELECT 
  memid, 
  telephone 
FROM 
  cd.members 
WHERE 
  telephone LIKE '%(%' 
  OR telephone LIKE '%)%';
```

###### Question 28: Count the number of members whose surname starts with each letter of the alphabet

```sql 
select 
  substr (mems.surname, 1, 1) as letter, 
  count(*) as count 
from 
  cd.members mems 
group by 
  letter 
order by 
  letter;
```



