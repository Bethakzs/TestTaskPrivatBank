CREATE TABLE task (
                      id SERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description VARCHAR(255) NOT NULL,
                      created_at TIMESTAMP,
                      deadline TIMESTAMP NOT NULL,
                      notified_one_hour BOOLEAN,
                      notified_ten_minutes BOOLEAN,
                      notified_deadline BOOLEAN,
                      status VARCHAR(50) NOT NULL
);
