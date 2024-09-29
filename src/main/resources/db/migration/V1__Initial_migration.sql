CREATE TABLE users(
                      id bigserial primary key,
                      created_at timestamp default CURRENT_TIMESTAMP,
                      updated_at timestamp default CURRENT_TIMESTAMP,
                      status varchar(50)
);

CREATE TABLE credit_cards(
                             id bigserial primary key ,
                             created_at timestamp default CURRENT_TIMESTAMP,
                             updated_at timestamp default CURRENT_TIMESTAMP,
                             account_id bigserial,
                             card_number varchar(255),
                             exp_date varchar(5),
                             cvv varchar(3)
);
CREATE TABLE customers(
    id bigserial primary key,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    first_name varchar(255),
    last_name varchar(255),
    country varchar(2),
    user_id bigserial,
    foreign key (user_id) REFERENCES users(id)
);

CREATE TABLE merchants(
    id bigserial primary key,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    merchant_name varchar(255) unique,
    password varchar(255),
    user_id bigserial,
    foreign key (user_id) references users(id)
);

CREATE TABLE accounts(
    id bigserial primary key,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    amount int,
    user_id bigserial,
    foreign key (user_id) references users(id)
);

CREATE TABLE transactions(
    id bigserial primary key,
    transaction_id uuid,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    transaction_type varchar(50),
    payment_method varchar(50),
    amount int,
    currency varchar(3),
    card_id bigserial,
    customer_id bigserial,
    language varchar(2),
    notification_url varchar(255),
    transaction_status varchar(50),
    account_from bigserial,
    account_to bigserial,
    foreign key (customer_id) references customers(id),
    foreign key (card_id) references credit_cards(id),
    foreign key (account_from) references accounts(id),
    foreign key (account_to) references accounts(id)
);

CREATE TABLE webhooks(
    id bigserial primary key,
    transaction_id uuid,
    created_at timestamp default CURRENT_TIMESTAMP,
    updated_at timestamp default CURRENT_TIMESTAMP,
    transaction_type varchar(50),
    payment_method varchar(50),
    currency varchar(3),
    card_id bigserial,
    language varchar(2),
    customer_id bigserial,
    web_hook_status varchar(50),
    message varchar(255),
    foreign key (customer_id) references customers(id),
    foreign key (card_id) references credit_cards(id)
)
