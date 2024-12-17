create table item
(
    id       serial constraint good_pkey primary key,
    name     varchar(20)  not null,
    size     varchar(255) not null,
    color    varchar(20)  not null,
    material varchar(20)  not null,
    price    integer      not null,
    quantity integer      not null,
    image    varchar(255)
);

INSERT INTO public.item (name, size, color, material, price, quantity, image) VALUES ('Заяц', 10, 'Красный', 'Плюш', 3200, 2, 'd0yi_IoDvmY.jpg');