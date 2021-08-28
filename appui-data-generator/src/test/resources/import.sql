drop database schedules;
drop database courses;
drop database tags;
drop database comments;
drop database notifications;
drop database parents
drop database teachers;
drop database students;
drop database bookings;
drop database availabilities;


create database schedules;
create database courses;
create database tags;
create database comments;
create database notifications;
create database parents;
create database teachers;
create database students;
create database bookings;
create database availabilities;


grant all privileges on database tags  to appui_scolaire_user;
grant all privileges on database schedules  to appui_scolaire_user;
grant all privileges on database  courses  to appui_scolaire_user;
grant all privileges on database  comments to appui_scolaire_user;
grant all privileges on database  notifications to appui_scolaire_user;
grant all privileges on database  parents to appui_scolaire_user;
grant all privileges on database  teachers to appui_scolaire_user;
grant all privileges on database  students to appui_scolaire_user;
grant all privileges on database  bookings to appui_scolaire_user;
grant all privileges on database  availabilities to appui_scolaire_user;