# SE-EDU Git Conventions

## Commit subject

- Write a meaningful subject for every commit.
- Aim for no more than 50 characters; never exceed 72 characters.
- Use the imperative mood, such as `Add README.md`.
- Capitalize the first letter.
- Do not end with a period.
- Add an optional `<scope>:` or `<category>:` prefix when useful, such as `Parser: Handle empty input` or `chore: Update release date`.

## Commit body

Include a body for every non-trivial commit.

- Separate the subject and body with a blank line.
- Wrap body text at 72 characters.
- Separate paragraphs with blank lines, and use bullet points when they improve clarity.
- Explain what changed and why; leave implementation details to the diff.
- Give enough context for a reviewer to judge the purpose without reading the diff.
- Avoid repeating information already documented in code comments.
- If the body becomes too long, consider splitting the work into smaller commits.

Use this structure when applicable:

1. Describe the situation in present tense. Avoid redundant qualifiers such as “currently” or “originally.”
2. Explain why it needs to change.
3. State what the commit does in the imperative mood. `Let's` may introduce this section.
4. Explain why that approach was chosen.
5. Add other relevant context.

## Branch names

- Use a meaningful kebab-case name made from relevant keywords, such as `refactor-ui-tests`.
- For work related to an issue, use `issueNumber-keywords-from-issue-title`, such as `1234-ui-freeze-error`.

Source: [SE-EDU Git conventions](https://se-education.org/guides/conventions/git.html)
