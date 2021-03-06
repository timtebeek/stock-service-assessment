create table STOCK (
    ID uuid PRIMARY KEY,
    PRODUCT uuid not null,
    BRANCH uuid not null,
    NUMBER_OF_ITEMS int not null,
    CONSTRAINT UC_STOCK UNIQUE (PRODUCT,BRANCH)
);

create table RESERVED_STOCK (
    ID uuid PRIMARY KEY,
    PRODUCT uuid not null,
    BRANCH uuid not null,
    NUMBER_OF_ITEMS int not null,

    CREATED_BY varchar(100) not null,
    CREATED_DATE timestamp not null,
    MODIFIED_BY varchar(100),
    MODIFIED_DATE timestamp
);
