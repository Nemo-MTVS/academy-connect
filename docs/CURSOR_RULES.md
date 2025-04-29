# Cursor Rules and Development Guidelines

## Code Style and Formatting

### 1. Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `ConsultingBooking`)
- **Methods**: camelCase (e.g., `getUserById`, `createBooking`)
- **Variables**: camelCase (e.g., `userId`, `bookingTime`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_SESSIONS`, `DEFAULT_TIMEOUT`)
- **Database Tables**: snake_case (e.g., `consulting_bookings`, `lunch_matchings`)

### 2. File Organization
- One primary class per file
- Related classes should be in the same package
- Keep file size manageable (preferably under 500 lines)
- Group related methods together

### 3. Code Documentation
- Add JavaDoc comments for public methods and classes
- Include parameter descriptions and return values
- Document non-obvious implementation details
- Keep comments up-to-date with code changes

## Project Structure Rules

### 1. Package Organization
```
store.mtvs.academyconnect/
├── domain/           # Domain entities and value objects
├── dto/              # Data Transfer Objects
├── repository/       # Data access layer
├── service/          # Business logic
├── controller/       # API endpoints
└── config/          # Configuration classes
```

### 2. Layer Separation
- Controllers should only communicate with Services
- Services handle business logic and coordinate repositories
- Repositories are the only classes that directly access data
- DTOs for data transfer between layers

## Database Conventions

### 1. Table Structure
- Use appropriate data types for columns
- Include audit fields (created_at, updated_at, deleted_at)
- Define proper foreign key relationships
- Use meaningful constraint names

### 2. Naming Rules
- Table names: plural, snake_case
- Primary keys: 'id' or table_name_id
- Foreign keys: referenced_table_singular_id
- Indexes: idx_table_name_column_name

## Git Workflow

### 1. Branch Naming
- Feature branches: `feature/description`
- Bug fixes: `fix/description`
- Hotfixes: `hotfix/description`
- Release branches: `release/version`

### 2. Commit Messages
Format: `type: subject`

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Formatting, missing semicolons, etc.
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance tasks

### 3. Pull Requests
- Create descriptive PR titles
- Include related issue numbers
- Add detailed descriptions of changes
- Request reviews from team members

## Testing Guidelines

### 1. Test Organization
- Match test package structure to main code
- Name test classes as `{ClassUnderTest}Test`
- Group related test methods together

### 2. Test Coverage
- Aim for minimum 80% code coverage
- Test both success and failure scenarios
- Include edge cases
- Mock external dependencies

## Security Practices

### 1. Data Protection
- Never commit sensitive data
- Use environment variables for credentials
- Encrypt sensitive user data
- Implement proper access controls

### 2. Input Validation
- Validate all user inputs
- Sanitize data before processing
- Use prepared statements for SQL
- Implement proper error handling

## Maintenance

### 1. Code Reviews
- Review all changes before merging
- Check for style compliance
- Verify test coverage
- Ensure documentation is updated

### 2. Regular Updates
- Keep dependencies up to date
- Monitor for security vulnerabilities
- Regular code cleanup
- Remove deprecated code

## IDE Configuration

### 1. Editor Settings
- Use spaces for indentation (4 spaces)
- Enable auto-formatting
- Show line numbers
- Show whitespace characters

### 2. Plugins
- SonarLint for code quality
- Checkstyle for style enforcement
- Git integration
- Database tools 