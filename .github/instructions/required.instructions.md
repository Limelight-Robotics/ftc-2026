---
applyTo: '**'
---
Use a clear, consistent coding style and spacing.

Rules:
- Keep functions short: maximum 20 lines each.
- Use descriptive variable names; avoid single-letter or vague names.
- Replace magic numbers with named constants that explain their meaning.
- Always include proper error handling (validate inputs, return or throw errors).
- Comment only to explain complex logic; avoid redundant comments.
- Maintain consistent indentation and formatting across files.
- Keep files under 200 lines; split into modules/files when needed.
- Run and pass all tests before committing.
- Use meaningful, descriptive commit messages.
- Regularly review and refactor code to improve readability and maintainability.
- Write code with future maintainers in mind; prioritize clarity over cleverness.
- Use a maximum argument count of 2 (hard limit is 3) for functions; consider using objects for more parameters.
- Limit nesting depth to 3 levels; refactor to reduce complexity if exceeded.
- Maximum of 3 return statements per function; refactor if more are needed.
- Keep line length under 120 characters; break long lines for readability.
- Use consistent naming conventions (e.g., camelCase for variables and functions, PascalCase for classes).
- Avoid deep inheritance hierarchies; prefer composition over inheritance.
- Ensure all public methods have clear documentation comments explaining their purpose, parameters, and return values.
