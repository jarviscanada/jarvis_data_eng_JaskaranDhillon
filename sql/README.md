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
###### Question 1: Show all members

```sql
SELECT *
FROM cd.members
```

###### Questions 2: Lorem ipsum...

```sql
SELECT blah blah 
```



