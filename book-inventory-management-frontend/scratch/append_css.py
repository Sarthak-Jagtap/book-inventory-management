import os

file_path = 'src/main/resources/static/css/style.css'
css_to_append = """

/* =============== STRUCTURAL UI UX COMPONENTS =============== */
/* Added to enforce clean layout borders across the application */

.card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.02);
  margin-bottom: 24px;
}
.card-header, .card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border);
}

.data-table-wrapper {
  overflow-x: auto;
}
.data-table {
  width: 100%;
  border-collapse: collapse;
}
.data-table th {
  text-align: left;
  padding: 12px 16px;
  background: var(--surface2);
  border-bottom: 2px solid var(--border);
  color: var(--text2);
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
}
.data-table td {
  padding: 16px;
  border-bottom: 1px solid var(--border);
  color: var(--text);
  vertical-align: middle;
}
.data-table tbody tr:hover {
  background: var(--surface2);
}

.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 8px;
}
.form-control {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 14px;
  color: var(--text);
  background: var(--surface);
  transition: border-color 0.2s;
}
.form-control:focus {
  outline: none;
  border-color: var(--accent);
}

.page-wrapper {
  max-width: 1100px;
  margin: 40px auto;
  padding: 0 24px;
}
.page-wrapper-sm {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 24px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
  flex-wrap: wrap;
  gap: 16px;
}
.page-title h1 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text);
}
.page-title p {
  color: var(--text2);
  margin-top: 4px;
}
"""

with open(file_path, 'a', encoding='utf-8') as f:
    f.write(css_to_append)
