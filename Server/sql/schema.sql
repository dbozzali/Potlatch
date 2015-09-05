create table FlaggedByUsers (GiftId bigint not null, Username varchar(32) not null, primary key (GiftId, Username))
create table Gifts (Id bigint not null, Content clob not null, CreationTimestamp bigint not null, FlagsCount integer not null, Text varchar(1024) not null, Thumbnail clob not null, Title varchar(255) not null, TouchesCount integer not null, CreatedBy varchar(32) not null, GiftChain bigint not null, primary key (Id))
create table TouchedUsers (GiftId bigint not null, Username varchar(32) not null, primary key (GiftId, Username))
create table Users (Username varchar(32) not null, Avatar clob, TotalTouchesCount bigint not null, primary key (Username))
alter table FlaggedByUsers add constraint FK_rsv7c08ixr821oxnghnstpw5h foreign key (Username) references Users
alter table FlaggedByUsers add constraint FK_qookqbec9prlg78qbt0cfv82g foreign key (GiftId) references Gifts
alter table Gifts add constraint FK_bk66ll5p36bad2cssw77wv3hd foreign key (CreatedBy) references Users
alter table Gifts add constraint FK_mwyn4vi1gy5vah4y5v6be4pfj foreign key (GiftChain) references Gifts
alter table TouchedUsers add constraint FK_l8fqht6ij7mjpoff3uerqgl60 foreign key (Username) references Users
alter table TouchedUsers add constraint FK_bjo2h9b05d5owsm4iakko605 foreign key (GiftId) references Gifts
create sequence hibernate_sequence
