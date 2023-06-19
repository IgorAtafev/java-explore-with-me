CREATE TABLE IF NOT EXISTS endpoint_hits (
  id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  app varchar(50) NOT NULL,
  uri varchar(255) NOT NULL,
  ip varchar(16) NOT NULL,
  created timestamp without time zone NOT NULL
);
