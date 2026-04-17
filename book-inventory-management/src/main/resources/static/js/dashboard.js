function toggleDark() {
  document.body.classList.toggle('dark');
  const isDark = document.body.classList.contains('dark');
  document.getElementById('dark-icon').textContent = isDark ? '☀️' : '🌙';
  document.getElementById('dark-label').textContent = isDark ? 'Light mode' : 'Dark mode';
}

function switchTab(id, el) {
  document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
  document.getElementById('tab-' + id).classList.add('active');
  el.classList.add('active');
}

function toggle(card) {
  card.classList.toggle('open');
}

function openApi(url) {
  window.location.href = '/api-author-result?endpoint=' + encodeURIComponent(url);
}

function doUpdate() {
  const id = document.getElementById('w-upd-id').value;
  if (!id) { alert('Please enter an Author ID'); return; }
  fetch('/api/v1/store-owner/authors/' + id, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ firstName: 'Updated', lastName: 'Author', photo: '' })
  })
  .then(r => r.json())
  .then(() => { openApi('/api/v1/authors/' + id); })
  .catch(() => alert('Request sent (check network tab for response)'));
}

function doDelete() {
  const id = document.getElementById('w-del-id').value;
  if (!id) { alert('Please enter an Author ID'); return; }
  if (!confirm('Delete author ' + id + '? This cannot be undone.')) return;
  fetch('/api/v1/store-owner/authors/' + id, { method: 'DELETE' })
    .then(() => alert('Author deleted successfully'))
    .catch(() => alert('Delete request sent'));
}

function doDeleteBA() {
  const isbn = document.getElementById('w-del-isbn').value;
  const aid  = document.getElementById('w-del-aid').value;
  if (!isbn || !aid) { alert('Please fill in both ISBN and Author ID'); return; }
  if (!confirm('Remove book-author mapping? This cannot be undone.')) return;
  fetch('/api/v1/store-owner/book-authors/' + isbn + '/' + aid, { method: 'DELETE' })
    .then(() => alert('Mapping removed successfully'))
    .catch(() => alert('Delete request sent'));
}
