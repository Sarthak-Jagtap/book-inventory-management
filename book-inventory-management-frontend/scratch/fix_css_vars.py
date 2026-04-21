import os

file_path = 'src/main/resources/templates/author/api-author-dashboard.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace wrong variables with the correct style.css variables
content = content.replace('var(--bg-white)', 'var(--surface)')
content = content.replace('var(--bg-page)', 'var(--surface2)')
content = content.replace('var(--text-dark)', 'var(--text)')
content = content.replace('var(--text-mid)', 'var(--text2)')
content = content.replace('var(--text-muted)', 'var(--text3)')
content = content.replace('var(--blue)', 'var(--accent)')
content = content.replace('var(--blue-light)', 'var(--surface2)')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
