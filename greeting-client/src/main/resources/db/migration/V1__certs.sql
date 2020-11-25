CREATE TABLE certs
(
    ID         serial,
    data       TEXT,
    alias      TEXT,
    store     TEXT,
    not_before TIMESTAMP WITH TIME ZONE,
    not_after  TIMESTAMP WITH TIME ZONE
);
