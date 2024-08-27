CREATE TABLE transactions(
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at timestamp not null default CURRENT_TIMESTAMP,
    updated_at timestamp not null default CURRENT_TIMESTAMP,
    transaction_type varchar(50),
    payment_method varchar(50),
    amount int,
    currency varchar(3),
    language varchar(2),
    notification_url varchar(255),
    transaction_status varchar(50),
    account_from bigserial,
    account_to bigserial
);