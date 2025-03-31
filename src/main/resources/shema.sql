CREATE TABLE Notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    snils VARCHAR(14) NOT NULL,
    header VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL
);
CREATE TABLE Users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    snils VARCHAR(14) NOT NULL UNIQUE,
    age INT NOT NULL,
    fullName VARCHAR(255) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    city VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    height INT NOT NULL,
    phoneNumber VARCHAR(20) NOT NULL,
    bloodGroup VARCHAR(10) NOT NULL
);