CREATE TABLE Notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    snils VARCHAR(14) NOT NULL,
    header VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL
);