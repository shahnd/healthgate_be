-- Initial schema for HealthGate.
-- This migration is the target schema baseline managed by Flyway.

CREATE TABLE departments (
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(30) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE positions (
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(30) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE employees (
    id BIGINT NOT NULL AUTO_INCREMENT,

    employee_number VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    name VARCHAR(20) NOT NULL,
    hire_date DATE NULL,
    email VARCHAR(100) NULL,
    phone VARCHAR(13) NULL,
    role ENUM('EMPLOYEE', 'HEALTH_ADMIN', 'HR_ADMIN') NULL,
    status VARCHAR(1) NULL,

    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    department_id BIGINT NULL,
    position_id BIGINT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_employees_employee_number UNIQUE (employee_number),
    CONSTRAINT fk_employees_department_id
        FOREIGN KEY (department_id) REFERENCES departments (id),
    CONSTRAINT fk_employees_position_id
        FOREIGN KEY (position_id) REFERENCES positions (id)
);

CREATE TABLE biometrics (
    id BIGINT NOT NULL AUTO_INCREMENT,

    measured_at DATETIME(6) NOT NULL,
    systolic_bp INT NOT NULL,
    diastolic_bp INT NOT NULL,
    temperature FLOAT NULL,
    heart_rate INT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,

    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    employee_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_biometrics_employee_id
        FOREIGN KEY (employee_id) REFERENCES employees (id)
);

CREATE TABLE risk_threshold_settings (
    id BIGINT NOT NULL AUTO_INCREMENT,

    metric_name VARCHAR(30) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    threshold_value FLOAT NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE checkups (
    checkup_id BIGINT NOT NULL AUTO_INCREMENT,

    checkup_year SMALLINT NOT NULL,
    checkup_date DATE NULL,
    checkup_summary TEXT NULL,

    checkup_created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    employee_id BIGINT NOT NULL,

    PRIMARY KEY (checkup_id),
    CONSTRAINT fk_checkups_employee_id
        FOREIGN KEY (employee_id) REFERENCES employees (id)
);

CREATE TABLE checkup_reminder_settings (
    checkup_reminder_setting_id BIGINT NOT NULL AUTO_INCREMENT,

    checkup_reminder_setting_type VARCHAR(30) NOT NULL,
    checkup_reminder_setting_message_template TEXT NOT NULL,
    checkup_reminder_setting_cron_schedule VARCHAR(30) NOT NULL,
    checkup_reminder_setting_is_active BIT(1) NOT NULL,

    PRIMARY KEY (checkup_reminder_setting_id)
);

CREATE TABLE checkup_reminders (
    checkup_reminder_id BIGINT NOT NULL AUTO_INCREMENT,

    checkup_reminder_channel ENUM('EMAIL', 'SMS') NOT NULL,
    checkup_reminder_content TEXT NOT NULL,
    checkup_reminder_status VARCHAR(20) NOT NULL,
    checkup_reminder_is_manual BIT(1) NOT NULL,
    checkup_reminder_sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    checkup_id BIGINT NOT NULL,

    PRIMARY KEY (checkup_reminder_id),
    CONSTRAINT fk_checkup_reminders_checkup_id
        FOREIGN KEY (checkup_id) REFERENCES checkups (checkup_id)
);

CREATE TABLE consultations (
    id BIGINT NOT NULL AUTO_INCREMENT,

    scheduled_date DATE NOT NULL,
    scheduled_turn CHAR(2) NOT NULL,
    reason VARCHAR(100) NOT NULL,
    content TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    consultated_at DATETIME NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    manager_id BIGINT NULL,
    employee_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_consultations_manager_id
        FOREIGN KEY (manager_id) REFERENCES employees (id),
    CONSTRAINT fk_consultations_employee_id
        FOREIGN KEY (employee_id) REFERENCES employees (id)
);

CREATE TABLE hospitals (
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(255) NOT NULL,
    address VARCHAR(500) NOT NULL,
    phone VARCHAR(20) NULL,
    url VARCHAR(255) NULL,
    description TEXT NULL,
    status CHAR(1) NOT NULL DEFAULT 'Y',
    is_general_exam_available BIT(1) NOT NULL,
    is_stomach_cancer_exam_available BIT(1) NOT NULL,
    is_colon_cancer_exam_available BIT(1) NOT NULL,
    is_liver_cancer_exam_available BIT(1) NOT NULL,
    is_lung_cancer_exam_available BIT(1) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id)
);

CREATE TABLE timecards (
    id BIGINT NOT NULL AUTO_INCREMENT,

    status VARCHAR(20) NOT NULL,
    clock_in_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    employee_id BIGINT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_timecards_employee_id
        FOREIGN KEY (employee_id) REFERENCES employees (id)
);

CREATE TABLE notices (
    id BIGINT NOT NULL AUTO_INCREMENT,

    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    status CHAR(1) NOT NULL DEFAULT 'Y',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    author_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_notices_author_id
        FOREIGN KEY (author_id) REFERENCES employees (id)
);

CREATE TABLE notice_files (
    id BIGINT NOT NULL AUTO_INCREMENT,

    origin_name VARCHAR(255) NOT NULL,
    saved_name VARCHAR(255) NOT NULL,
    saved_path VARCHAR(255) NOT NULL,
    extension VARCHAR(255) NOT NULL,

    notice_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_notice_files_notice_id
        FOREIGN KEY (notice_id) REFERENCES notices (id)
);

CREATE TABLE safety_documents (
    id BIGINT NOT NULL AUTO_INCREMENT,

    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    original_filename VARCHAR(512) NOT NULL,
    storage_key VARCHAR(512) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    content_checksum VARCHAR(128) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL,

    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    created_by_employee_id BIGINT NOT NULL,
    updated_by_employee_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_safety_documents_content_checksum UNIQUE (content_checksum),
    CONSTRAINT uk_safety_documents_storage_key UNIQUE (storage_key),
    CONSTRAINT fk_safety_documents_created_by_employee_id
        FOREIGN KEY (created_by_employee_id) REFERENCES employees (id),
    CONSTRAINT fk_safety_documents_updated_by_employee_id
        FOREIGN KEY (updated_by_employee_id) REFERENCES employees (id)
);

CREATE TABLE vector_index_manifests (
    fingerprint VARCHAR(128) NOT NULL,

    content_checksum VARCHAR(128) NOT NULL,
    chunk_count INT NULL,
    status ENUM('COMPLETED', 'FAILED', 'INDEXING', 'PENDING', 'PURGE_FAILED', 'PURGING') NOT NULL,
    failure_message VARCHAR(1000) NULL,

    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (fingerprint)
);

CREATE TABLE weather_forecasts (
    id BIGINT NOT NULL AUTO_INCREMENT,

    forecast_at DATETIME(6) NULL,
    location ENUM('APGUJEONG', 'CHEONGDAM', 'DAECHI1', 'DAECHI2', 'DAECHI4', 'DOGOK1', 'DOGOK2', 'GAEPO1', 'GAEPO2', 'GAEPO3', 'GAEPO4', 'ILWON1', 'ILWONBON', 'NONHYEON1', 'NONHYEON2', 'SAMSEONG1', 'SAMSEONG2', 'SEGOK', 'SINSA', 'SUSEO', 'YEOKSAM1', 'YEOKSAM2') NULL,
    temperature DECIMAL(38, 2) NULL,
    humidity DECIMAL(38, 2) NULL,
    precipitation_probability DECIMAL(38, 2) NULL,
    wind_speed DECIMAL(38, 2) NULL,
    precipitation VARCHAR(255) NULL,
    snowfall VARCHAR(255) NULL,
    precipitation_type ENUM('NONE', 'RAIN', 'RAIN_SNOW', 'SHOWER', 'SNOW') NULL,
    sky_condition ENUM('CLEAR', 'CLOUDY', 'PARTLY_CLOUDY') NULL,

    created_at DATETIME(6) NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_weather_forecasts_forecast_location
        UNIQUE (forecast_at, location)
);

CREATE TABLE safety_briefings (
    id BIGINT NOT NULL AUTO_INCREMENT,

    briefing_date DATE NOT NULL,
    context_fingerprint VARCHAR(64) NOT NULL,
    content TEXT NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT uk_safety_briefings_date_context
        UNIQUE (briefing_date, context_fingerprint)
);
