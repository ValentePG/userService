create table user_service.tbl_profile
(
    id          bigint auto_increment
        primary key,
    description varchar(255) not null,
    name        varchar(255) not null,
    constraint UKcin5qreyfdxtrwma2x6ex29k2
        unique (name)
);

create table user_service.tbl_usuario
(
    id         bigint auto_increment
        primary key,
    email      varchar(255) null,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    password   varchar(255) not null,
    roles      varchar(255) not null,
    constraint UKdnvgup8yi2egdo5j5ekna9272
        unique (email)
);

create table user_service.user_profile
(
    id         bigint auto_increment
        primary key,
    profile_id bigint not null,
    user_id    bigint not null,
    constraint FKa92b4ik7ui3nw9173ted4hsra
        foreign key (profile_id) references user_service.tbl_profile (id),
    constraint FKpdt30hkno1e0qrgtj3ymv6bdo
        foreign key (user_id) references user_service.tbl_usuario (id)
);

create table user_service.tbl_usuario_user_profiles
(
    user_id          bigint not null,
    user_profiles_id bigint not null,
    primary key (user_id, user_profiles_id),
    constraint UKhlspq5t2m2i0x4pa5ol3ckc8j
        unique (user_profiles_id),
    constraint FKntcakfn2jlwvx577kvubt6rks
        foreign key (user_profiles_id) references user_service.user_profile (id),
    constraint FKsv5be2y383t0pia6fvm2bo2cp
        foreign key (user_id) references user_service.tbl_usuario (id)
);

