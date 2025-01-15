insert into tbl_usuario (id, email, first_name, last_name)
values (1, 'yusuke@gmail.com', 'yusuke', 'urameshi');
insert into tbl_usuario (id, email, first_name, last_name)
values (2, 'hiei@gmail.com', 'hiei', 'dragon');
insert into tbl_profile (id, description, name)
values (1, 'Manages everything', 'Admin');
insert into user_profile(id, user_id, profile_id)
values (1, 1, 1);
insert into user_profile(id, user_id, profile_id)
values (2, 2, 1);