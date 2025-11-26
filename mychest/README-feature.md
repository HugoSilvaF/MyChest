# MyChest Feature Module

This document describes how to integrate the MySQL persistence feature module into the MyChest plugin.

## Overview

The feature module provides:
- **Chest Limits**: Custom per-player slot limits
- **Chest Sharing**: Share chests with other players (VIEW/EDIT permissions)
- **Audit Logging**: Track all chest operations for security and debugging
- **Database Management**: HikariCP connection pool for optimal performance

## Dependencies

Add the following dependencies to your `pom.xml`:

```xml
<!-- HikariCP Connection Pool -->
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>
```

## Configuration

Add the following to your `config.yml`:

```yaml
# MySQL Configuration
mysql:
  host: localhost
  port: 3306
  database: mychest
  user: root
  password: your_password
  poolSize: 10
  useSSL: false

# Feature Module Settings
settings:
  # Default number of slots for new players
  default_slots: 27
  # Number of days to retain audit logs
  audit_retention_days: 30
```

## Integration

### 1. Initialize in Main Plugin

In your main plugin class `onEnable()` method:

```java
import com.github.yannicklampers.mychest.feature.database.DatabaseManager;
import com.github.yannicklampers.mychest.feature.dao.*;
import com.github.yannicklampers.mychest.feature.service.*;
import com.github.yannicklampers.mychest.feature.commands.*;

public class Main extends JavaPlugin {
    
    private DatabaseManager databaseManager;
    private ChestLimitService limitService;
    private ShareService shareService;
    private AuditLoggerService auditService;
    
    @Override
    public void onEnable() {
        // ... existing initialization code ...
        
        // Initialize the feature module database manager
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();
        
        // Optional: Run embedded schema to create tables
        // Only call this if you want to auto-create tables on startup
        // databaseManager.runEmbeddedSchemaIfPresent();
        
        // Create DAOs
        LimitDaoMySQL limitDao = new LimitDaoMySQL(databaseManager.getDataSource(), getLogger());
        ShareDaoMySQL shareDao = new ShareDaoMySQL(databaseManager.getDataSource(), getLogger());
        AuditDaoMySQL auditDao = new AuditDaoMySQL(databaseManager.getDataSource(), getLogger());
        
        // Create Services
        limitService = new ChestLimitService(limitDao, getConfig());
        shareService = new ShareService(shareDao);
        auditService = new AuditLoggerService(auditDao, getLogger(), getConfig());
        
        // Register Commands
        getCommand("setlimit").setExecutor(new AdminSetLimitCommand(limitService, auditService));
        getCommand("sharechest").setExecutor(new UserShareCommand(shareService, auditService));
        
        // ... rest of existing code ...
    }
    
    @Override
    public void onDisable() {
        // ... existing disable code ...
        
        // Close the database connection pool
        if (databaseManager != null) {
            databaseManager.close();
        }
    }
}
```

### 2. Register Commands in plugin.yml

Add the new commands to your `plugin.yml`:

```yaml
commands:
  setlimit:
    description: Set chest slot limit for a player
    usage: /<command> <player> <slots|reset>
    permission: mychest.admin.setlimit
  sharechest:
    description: Share a chest with another player
    usage: /<command> <chestId> <player> [VIEW|EDIT]
    permission: mychest.share

permissions:
  mychest.admin.setlimit:
    description: Allows setting chest limits for players
    default: op
  mychest.share:
    description: Allows sharing chests with other players
    default: true
```

## Database Schema

The schema file is located at `resources/sql/schema.sql` and creates the following tables:

| Table | Description |
|-------|-------------|
| `chest_limits` | Stores custom slot limits per player (player_id CHAR(36) PK) |
| `chest_shares` | Stores chest sharing relationships (chest_id, owner_id, target_id) |
| `chest_audit` | Stores audit logs for all operations |
| `chest_backups` | Stores chest backup data as JSON |

### Running the Schema

**Option 1: Automatic (Not recommended for production)**
```java
// In onEnable(), after databaseManager.initialize():
databaseManager.runEmbeddedSchemaIfPresent();
```

**Option 2: Manual (Recommended)**
Execute the SQL file directly on your MySQL server:
```bash
mysql -u root -p mychest < sql/schema.sql
```

Or copy the contents of `sql/schema.sql` and execute in your MySQL client.

## Usage Examples

### Setting Limits (Admin)
```
/setlimit PlayerName 54      # Set 54 slots for PlayerName
/setlimit PlayerName reset   # Reset to default
```

### Sharing Chests (Players)
```
/sharechest 1 PlayerName           # Share chest #1 with VIEW permission
/sharechest 1 PlayerName EDIT      # Share chest #1 with EDIT permission
/sharechest revoke 1 PlayerName    # Revoke access
/sharechest list 1                 # List who has access to chest #1
```

## API Usage

### Using Services Programmatically

```java
// Get slot limit for a player
int slots = limitService.getSlotLimit(playerId);

// Check if player has custom limit
boolean hasCustom = limitService.hasCustomLimit(playerId);

// Share a chest
shareService.shareChest(chestId, ownerId, targetId, ShareService.PERMISSION_EDIT);

// Check access
boolean canAccess = shareService.hasAccess(chestId, playerId);
boolean canEdit = shareService.canEdit(chestId, playerId);

// Log actions
auditService.log(playerId, "CUSTOM_ACTION", "Details here");

// Cleanup old audit logs
int deleted = auditService.cleanupOldLogs();
```

## File Structure

```
mychest/src/main/java/com/github/yannicklampers/mychest/
├── dao/
│   ├── LimitDao.java          # Interface for limit operations
│   ├── ShareDao.java          # Interface for share operations
│   └── AuditDao.java          # Interface for audit operations
└── feature/
    ├── database/
    │   └── DatabaseManager.java   # HikariCP pool manager
    ├── dao/
    │   ├── LimitDaoMySQL.java     # MySQL limit implementation
    │   ├── ShareDaoMySQL.java     # MySQL share implementation
    │   └── AuditDaoMySQL.java     # MySQL audit implementation
    ├── service/
    │   ├── ChestLimitService.java     # Limit business logic
    │   ├── ShareService.java          # Share business logic
    │   └── AuditLoggerService.java    # Audit business logic
    └── commands/
        ├── AdminSetLimitCommand.java  # /setlimit command
        └── UserShareCommand.java      # /sharechest command

mychest/src/main/resources/
└── sql/
    └── schema.sql             # Database schema
```

## Notes

- The `DatabaseManager.runEmbeddedSchemaIfPresent()` method does **NOT** run automatically. You must call it explicitly in `onEnable()` if you want automatic table creation.
- UUIDs are stored as `CHAR(36)` strings for compatibility.
- The audit retention cleanup should be scheduled (e.g., daily task) or called manually.
- All SQL operations use prepared statements to prevent SQL injection.

## Troubleshooting

### Connection Pool Errors
- Verify MySQL credentials in `config.yml`
- Ensure MySQL server is running and accessible
- Check firewall rules if connecting remotely

### Schema Errors
- Run the schema manually if automatic execution fails
- Ensure the database exists before running the schema
- Check MySQL version compatibility (tested with MySQL 8.0+)

### Permission Issues
- Verify permissions are defined in `plugin.yml`
- Check player has the required permission node
- Use a permissions plugin like LuckPerms for fine-grained control
