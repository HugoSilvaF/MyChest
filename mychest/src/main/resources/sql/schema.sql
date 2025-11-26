-- MyChest Feature Module Schema
-- This schema creates the tables required for the limits, shares, audit, and backups features.

-- Table: chest_limits
-- Stores custom slot limits per player
CREATE TABLE IF NOT EXISTS chest_limits (
    player_id CHAR(36) NOT NULL PRIMARY KEY,
    slots INT NOT NULL DEFAULT 27,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: chest_shares
-- Stores chest sharing relationships between players
CREATE TABLE IF NOT EXISTS chest_shares (
    id INT AUTO_INCREMENT PRIMARY KEY,
    chest_id INT NOT NULL,
    owner_id CHAR(36) NOT NULL,
    target_id CHAR(36) NOT NULL,
    permissions VARCHAR(16) NOT NULL DEFAULT 'VIEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_share (chest_id, target_id),
    INDEX idx_chest_id (chest_id),
    INDEX idx_owner_id (owner_id),
    INDEX idx_target_id (target_id)
);

-- Table: chest_audit
-- Stores audit logs for chest operations
CREATE TABLE IF NOT EXISTS chest_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_id CHAR(36) NOT NULL,
    action VARCHAR(64) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_player_id (player_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at)
);

-- Table: chest_backups
-- Stores chest backup data as JSON for recovery purposes
CREATE TABLE IF NOT EXISTS chest_backups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chest_id INT NOT NULL,
    player_id CHAR(36) NOT NULL,
    backup_data JSON NOT NULL,
    backup_type VARCHAR(32) NOT NULL DEFAULT 'AUTO',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_chest_id (chest_id),
    INDEX idx_player_id (player_id),
    INDEX idx_created_at (created_at)
);
