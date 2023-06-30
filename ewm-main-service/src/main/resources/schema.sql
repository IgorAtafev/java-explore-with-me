DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS participation_requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email varchar(255) NOT NULL UNIQUE,
  name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
  id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
  id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title varchar(255) NOT NULL,
  annotation varchar(2000) NOT NULL,
  description varchar(7000) NOT NULL,
  event_date timestamp without time zone NOT NULL,
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  category_id bigint NOT NULL REFERENCES categories (id),
  initiator_id bigint NOT NULL REFERENCES users (id),
  paid boolean NOT NULL DEFAULT false,
  participant_limit INT NOT NULL DEFAULT 0,
  request_moderation boolean NOT NULL DEFAULT true,
  published timestamp without time zone,
  state varchar(20) NOT NULL,
  created timestamp without time zone NOT NULL
);

CREATE TABLE IF NOT EXISTS participation_requests (
  id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  event_id bigint NOT NULL REFERENCES events (id),
  requester_id bigint NOT NULL REFERENCES users (id),
  status varchar(20) NOT NULL,
  created timestamp without time zone NOT NULL
);
