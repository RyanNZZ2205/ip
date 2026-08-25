# Basic and intermediate rules

## Naming

- Use lowercase package names rooted in the project or group name.
- Name classes and enums with English nouns in PascalCase.
- Name variables in camelCase and methods with English verbs in camelCase.
- Name constants in SCREAMING_SNAKE_CASE; give associated constants a common prefix.
- In names, treat abbreviations and acronyms as words (`exportHtml`, not `exportHTML`).
- Test methods may use `featureUnderTest_testScenario_expectedBehavior`; the latter parts may be omitted.
- Give large-scope variables descriptive names. Short scratch names are acceptable only in a small scope. Use `i` for the first iterator and `j`, `k`, and so on only for nested loops.
- Name booleans so they read as booleans, preferably with prefixes such as `is`, `has`, `was`, `can`, or `should`. Boolean setters take the form `setFound(boolean isFound)`.
- Use plural names for collections.

## Layout

- Indent with 4 spaces and never tabs. Indent wrapped lines 8 spaces beyond their parent line.
- Keep lines below the 120-character hard limit and preferably below 110 characters.
- Wrap for readability: generally break after commas and before operators (including `.`, `&` in type bounds, and `|` in multi-catch). Keep a method name attached to its opening parenthesis and prefer higher-level breaks.
- Use K&R braces. Apply the standard block layouts to methods, conditionals, loops, `switch`, and `try`/`catch`/`finally` statements.
- Surround operators with spaces; put a space after Java keywords, commas, and `for` semicolons; surround ternary colons with spaces.
- Separate logical units inside a block with one blank line.

## Statements

- Put every class in a package.
- Keep import ordering consistent, explicit, minimal, and free of wildcard imports.
- Attach array brackets to the type (`int[] values`).
- Initialize variables at declaration when possible and declare them in the smallest useful scope.
- Do not expose class variables as `public` unless the class is a behavior-free data class; constants are exempt.
- Always put loop and conditional bodies on separate lines and inside braces, including single-statement bodies.
- Add `// Fallthrough` whenever a traditional `switch` case intentionally continues into the next case.

## Comments and Javadocs

- Write comments in English using American spelling and no local slang.
- Add descriptive Javadocs to every class and public method, except getters/setters, exact overrides, and test classes/methods.
- Start a Javadoc with a short summary sentence phrased in third-person form (`Returns`, `Adds`, `Sends`).
- Format multi-line Javadocs with `/**` on its own line, aligned `*` markers, a space after `*`, and no blank line before the declaration.
- Put one blank Javadoc line between the description and tags. End parameter descriptions with punctuation. Include either all `@param` tags or none, and omit tags that add no information.
- Indent comments with the surrounding code. Trailing comments are allowed.
