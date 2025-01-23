create table client
(
    id        uuid primary key default gen_random_uuid(),
    last_name        varchar(30),
    first_name       varchar(30),
    middle_name      varchar(30),
    birth_day        date,
    email            varchar,
    gender           varchar,
    marital_status   varchar,
    dependent_amount int,
    passport_id      jsonb,
    employment_id    jsonb,
    account_number   varchar
);
create table credit
(
    id         uuid primary key default gen_random_uuid(),
    amount            decimal,
    term              int,
    monthly_payment   decimal,
    rate              decimal,
    psk               decimal,
    payment_schedule  jsonb,
    insurance_enabled boolean,
    salary_client     boolean,
    credit_status     varchar
);

create table statement
(
    id   uuid primary key default gen_random_uuid(),
    client_id      uuid references client (id),
    credit_id      uuid references credit (id),
    status         varchar,
    creation_date  timestamp,
    applied_offer  jsonb,
    sign_date      timestamp,
    ses_code       varchar,
    status_history jsonb
);