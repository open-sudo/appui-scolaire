
ALTER SYSTEM SET wal_level = logical;

create database target;
\c target
CREATE TABLE demo (id integer);


create database source;
\c source
CREATE TABLE demo (id integer);


CREATE PUBLICATION my_publication FOR table demo;



select pg_create_logical_replication_slot('demo', 'pgoutput');
\c target
CREATE SUBSCRIPTION my_subscription CONNECTION 'host=localhost port=5432 dbname=source' PUBLICATION my_publication WITH (create_slot=false, slot_name='demo');

\c source

insert into demo values (20);
select * from demo;

\c target

select * from demo;






 
\c target
CREATE SUBSCRIPTION my_subscription CONNECTION 'host=localhost port=5432 dbname=source' PUBLICATION my_publication WITH (create_slot=false, slot_name='demo');



CREATE PUBLICATION my_publication FOR ALL TABLES;
