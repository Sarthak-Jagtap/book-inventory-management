import os
import re

file_path = 'src/main/resources/templates/author/api-author-dashboard.html'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace <style> up to <body class="dark"> and <div class="topbar">...</div>
pattern = r'<style>.*?</style>\s*</head>\s*<body class="dark">\s*<div class="topbar">.*?</div>'
replacement = """<link rel="stylesheet" th:href="@{/css/style.css}"/>
<style>
/* Specific definitions to keep the interactive API Explorer working */
.hero{padding:40px 32px 0;display:flex;align-items:flex-end;gap:32px;flex-wrap:wrap}
.hero-text h1{font-size:28px;font-weight:700;color:var(--text-dark);line-height:1.2}
.hero-text p{color:var(--text-mid);font-size:14px;margin-top:6px;max-width:520px;line-height:1.6}
.stats{display:flex;gap:12px;margin-top:20px;flex-wrap:wrap}
.stat{ background:var(--bg-white);border:1px solid var(--border); padding:12px 18px;border-radius:10px;text-align:center;}
.stat-n{font-size:22px;font-weight:700;color:var(--blue)}
.stat-l{font-size:11px;color:var(--text-muted);text-transform:uppercase;letter-spacing:.5px;margin-top:2px}
.tabs{ display:flex;gap:4px;padding:28px 32px 0; border-bottom:1px solid var(--border);overflow-x:auto;}
.tab{ padding:8px 18px;border-radius:8px 8px 0 0;cursor:pointer; font-size:13px;font-weight:500;color:var(--text-mid); border:1px solid transparent;border-bottom:none; transition:all .2s;white-space:nowrap;}
.tab.active{ background:var(--bg-white);color:var(--blue); border-color:var(--border);border-bottom-color:var(--bg-white); position:relative;top:1px;}
.tab:hover:not(.active){color:var(--text-dark);background:var(--bg-page)}
.tab-dot{width:7px;height:7px;border-radius:50%;display:inline-block;margin-right:6px}
.content{padding:28px 32px}
.section{display:none;animation:fadeIn .25s ease}
.section.active{display:block}
@keyframes fadeIn{from{opacity:0;transform:translateY(6px)}to{opacity:1;transform:translateY(0)}}
.section-desc{ background:var(--blue-light);border-left:3px solid var(--blue); padding:12px 16px;border-radius:0 8px 8px 0; font-size:13px;color:var(--text-mid);margin-bottom:20px;line-height:1.6;}
.section-desc strong{color:var(--text-dark)}
.section-desc code{ background:var(--border);padding:1px 5px;border-radius:4px; font-family:monospace;font-size:12px;color:var(--blue);}
.endpoint-card.open .chevron{transform:rotate(180deg)}
.ep-header{display:flex;align-items:center;gap:12px;padding:14px 16px;cursor:pointer;user-select:none}
.ep-path{font-family:'Courier New',monospace;font-size:13px;color:var(--text-dark);flex:1}
.ep-desc-inline{font-size:12px;color:var(--text-muted);margin-left:auto;padding-right:8px}
.chevron{color:var(--text-muted);font-size:14px;transition:transform .25s;flex-shrink:0}
.ep-body{ border-top:1px solid var(--border);padding:16px; background:var(--bg-page);display:none;}
.endpoint-card.open .ep-body{display:block;animation:fadeIn .2s ease}
.ep-body-desc{font-size:13px;color:var(--text-mid);margin-bottom:14px;line-height:1.6}
.params-grid{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:12px}
.param-item{ background:var(--bg-white);border:1px solid var(--border); padding:10px 12px;border-radius:8px;}
.param-name{font-family:monospace;font-size:12px;color:var(--blue);margin-bottom:2px}
.param-desc{font-size:12px;color:var(--text-mid)}
.param-type{font-size:10px;color:var(--text-muted);margin-top:2px}
.input-row{display:flex;align-items:center;gap:8px;flex-wrap:wrap;margin-bottom:10px}
.ep-label{font-size:12px;color:var(--text-muted);min-width:60px}
.ep-input{ background:var(--bg-white);border:1px solid var(--border); color:var(--text-dark);font-size:13px;padding:7px 12px;border-radius:8px; outline:none;width:200px;transition:border-color .2s;}
.ep-input:focus{border-color:var(--blue)}
.method-section-label{ font-size:11px;font-weight:600;text-transform:uppercase;letter-spacing:.8px; color:var(--text-muted);margin:24px 0 10px; padding-bottom:6px;border-bottom:1px solid var(--border);}
.try-note{font-size:11px;color:var(--text-muted);margin-top:8px;font-style:italic}
@media(max-width:600px){
  .hero{padding:24px 16px 0} .tabs{padding:20px 16px 0} .content{padding:20px 16px}
  .params-grid{grid-template-columns:1fr} .ep-desc-inline{display:none} .ep-input{width:140px} .hero-text h1{font-size:22px}
}
</style>
</head>
<body>

<div th:replace="~{fragments/navbar :: navbar}"></div>"""

content = re.sub(pattern, replacement, content, flags=re.DOTALL)

# Add type="button" to buttons
content = content.replace('<button class="run-btn"', '<button type="button" class="btn btn-primary"')
content = content.replace('class="ep-input"', 'type="text" class="ep-input"')

# Remove dark mode script
content = re.sub(r'document\.addEventListener\(\'DOMContentLoaded\'.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'function toggleDark\(\).*?\}', '', content, flags=re.DOTALL)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
