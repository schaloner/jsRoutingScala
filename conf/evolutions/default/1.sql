# --- !Ups

create table person (
  id bigint not null,
  name varchar(255),
  constraint pk_person primary key (id))
;

create sequence person_seq;


# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists person;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists person_seq;

