# --- Second database schema

# --- !Ups

CREATE SEQUENCE lesson_id_seq;
create table Lessons (
  id 			    integer not null default nextval('lesson_id_seq') primary key,
  name        varchar(255)
);

CREATE SEQUENCE bilet_id_seq;
create table Bilets (
  id 			    integer not null default nextval('bilet_id_seq') primary key,
  lesson_id   integer,
  num         int
);
CREATE SEQUENCE question_id_seq;
create table Questions (
  id 			    integer not null default nextval('question_id_seq') primary key,
  bilet_id    integer,
  quest_type  integer,
  typ 	      integer,
  text        text,
  image       varchar,
  answer      integer
);
CREATE SEQUENCE variant_id_seq;
create table Variants (
  id 			    integer not null default nextval('variant_id_seq') primary key,
  question_id int,
  text        varchar(255)
);
create table Stat (
  user_id     integer,
  lesson_id   integer,
  bilet_id    integer,
  question_id integer,
  answer      integer
);
create table BiletStat (
  user_id    integer,
  bilet_id   integer,
  perc	     integer
)
# --- !Downs
drop table if exists Lessons;
DROP SEQUENCE lesson_seq_id;
drop table if exists Bilets;
DROP SEQUENCE bilet_seq_id;
drop table if exists Questions;
DROP SEQUENCE question_seq_id;
drop table if exists Variants;
DROP SEQUENCE variant_seq_id;
drop table if exists Stat;
drop table if exists BiletStat;