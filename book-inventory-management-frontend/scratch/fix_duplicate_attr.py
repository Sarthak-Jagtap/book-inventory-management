import os
import re

file_path = 'src/main/resources/templates/author/api-author-dashboard.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace duplicate type="text"
content = re.sub(r'type="text"\s+type="text"', 'type="text"', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
