CREATE TABLE IF NOT EXISTS kill_death_data (
    uuid VARCHAR(64) NOT NULL,
    name VARCHAR(36) NOT NULL,
    kills INT DEFAULT 0,
    deaths INT DEFAULT 0 ,
    daily_kills INT DEFAULT 0,
    monthly_kills INT DEFAULT 0,
    yearly_kills INT DEFAULT 0,
    last_updated BIGINT DEFAULT -1
);